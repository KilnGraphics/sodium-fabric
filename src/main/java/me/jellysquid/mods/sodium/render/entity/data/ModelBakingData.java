package me.jellysquid.mods.sodium.render.entity.data;

import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.jellysquid.mods.sodium.SodiumClientMod;
import me.jellysquid.mods.sodium.interop.vanilla.layer.MultiPhaseAccessor;
import me.jellysquid.mods.sodium.interop.vanilla.layer.MultiPhaseParametersAccessor;
import me.jellysquid.mods.sodium.interop.vanilla.layer.RenderPhaseAccessor;
import me.jellysquid.mods.sodium.interop.vanilla.model.BufferBackedModel;
import me.jellysquid.mods.sodium.render.stream.StreamingBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;

import java.io.Closeable;
import java.util.*;

public class ModelBakingData implements Closeable, Iterable<Map<RenderLayer, Map<BufferBackedModel, InstanceBatch>>> {

    private static final int INITIAL_BATCH_CAPACITY = 16;

    /**
     * Slice current instances on transparency where order is required.
     * Looks more correct, but may impact performance greatly.
     */
    public static final boolean TRANSPARENCY_SLICING = true;

    private final Map<RenderLayer, Map<BufferBackedModel, InstanceBatch>> opaqueSection;
    /**
     * Each map in the deque represents a separate ordered section, which is required for transparency ordering.
     * For each model in the map, it has its own map where each RenderLayer has a list of instances. This is
     * because we can only batch instances with the same RenderLayer and model.
     */
    private final Deque<Map<RenderLayer, Map<BufferBackedModel, InstanceBatch>>> orderedTransparencySections;

    private final Set<AutoCloseable> closeables;
    private final Deque<InstanceBatch> batchPool;

    private final StreamingBuffer partBuffer;

    private RenderPhase.Transparency previousTransparency;

    public ModelBakingData(StreamingBuffer partBuffer) {
        this.partBuffer = partBuffer;
        opaqueSection = new LinkedHashMap<>();
        orderedTransparencySections = new ArrayDeque<>(64);
        closeables = new ObjectOpenHashSet<>();
        batchPool = new ArrayDeque<>(64);
    }

    @SuppressWarnings("ConstantConditions")
    public InstanceBatch getOrCreateInstanceBatch(RenderLayer renderLayer, BufferBackedModel model) {
        Map<RenderLayer, Map<BufferBackedModel, InstanceBatch>> renderSection;
        // we still use linked maps in here to try to preserve patterns for things that rely on rendering in order not based on transparency.
        MultiPhaseParametersAccessor multiPhaseParameters = (MultiPhaseParametersAccessor) (Object) ((MultiPhaseAccessor) renderLayer).getPhases();
        RenderPhase.Transparency currentTransparency = multiPhaseParameters.getTransparency();
        if (!(currentTransparency instanceof RenderPhaseAccessor currentTransparencyAccessor) || currentTransparencyAccessor.getName().equals("no_transparency")) {
            renderSection = opaqueSection;
        } else {
            if (orderedTransparencySections.size() == 0) {
                addNewSplit();
            } else if (TRANSPARENCY_SLICING && previousTransparency instanceof RenderPhaseAccessor previousTransparencyAccessor) {
                String currentTransparencyName = currentTransparencyAccessor.getName();
                String previousTransparencyName = previousTransparencyAccessor.getName();
                if (currentTransparencyName.equals(previousTransparencyName)) {
                    addNewSplit();
                }
            }

            renderSection = orderedTransparencySections.peekLast();
        }
        previousTransparency = currentTransparency;

        return renderSection
                .computeIfAbsent(renderLayer, unused -> new LinkedHashMap<>())
                .computeIfAbsent(model, model1 -> {
                    InstanceBatch recycledBatch = batchPool.pollFirst();
                    if (recycledBatch != null) {
                        recycledBatch.reset(model1, requiresIndexing(multiPhaseParameters), partBuffer);
                        return recycledBatch;
                    } else {
                        return new InstanceBatch(model1, requiresIndexing(multiPhaseParameters), INITIAL_BATCH_CAPACITY, partBuffer);
                    }
                });
    }

    public void recycleInstanceBatch(InstanceBatch instanceBatch) {
        batchPool.add(instanceBatch);
    }

    private void addNewSplit() {
        orderedTransparencySections.add(new LinkedHashMap<>());
    }

    private void writeSplitData(Map<RenderLayer, Map<BufferBackedModel, InstanceBatch>> splitData, StreamingBuffer modelBuffer, StreamingBuffer translucentElementBuffer) {
        for (Map<BufferBackedModel, InstanceBatch> perRenderLayerData : splitData.values()) {
            for (Map.Entry<BufferBackedModel, InstanceBatch> perModelData : perRenderLayerData.entrySet()) {
                InstanceBatch instanceBatch = perModelData.getValue();
                VertexBufferAccessor vertexBufferAccessor = (VertexBufferAccessor) perModelData.getKey().getVertexBuffer();
                instanceBatch.writeInstancesToBuffer(modelBuffer);
                instanceBatch.writeElements(vertexBufferAccessor.getDrawMode(), translucentElementBuffer);
            }
        }
    }

    public void writeData() {
        for (Map<RenderLayer, Map<BufferBackedModel, InstanceBatch>> perOrderedSectionData : this) {
            for (Map<BufferBackedModel, InstanceBatch> perRenderLayerData : perOrderedSectionData.values()) {
                for (InstanceBatch instanceBatch : perRenderLayerData.values()) {
                    if (instanceBatch.size() > 0) {
                    }
                }
            }
        }

        writeSplitData(opaqueSection);

        for (Map<RenderLayer, Map<BufferBackedModel, InstanceBatch>> transparencySection : orderedTransparencySections) {
            writeSplitData(transparencySection);
        }
    }

    public void addCloseable(AutoCloseable closeable) {
        closeables.add(closeable);
    }

    public Iterator<Map<RenderLayer, Map<BufferBackedModel, InstanceBatch>>> iterator() {
        return Iterators.concat(Iterators.singletonIterator(opaqueSection), orderedTransparencySections.iterator());
    }

    public boolean isEmptyShallow() {
        return opaqueSection.isEmpty() && orderedTransparencySections.isEmpty();
    }

    @SuppressWarnings("unused")
    public boolean isEmptyDeep() {
        for (Map<RenderLayer, Map<BufferBackedModel, InstanceBatch>> perOrderedSectionData : this) {
            for (Map<BufferBackedModel, InstanceBatch> perRenderLayerData : perOrderedSectionData.values()) {
                for (InstanceBatch instanceBatch : perRenderLayerData.values()) {
                    if (instanceBatch.size() > 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void reset() {
        opaqueSection.clear();
        orderedTransparencySections.clear();
    }

    @Override
    public void close() {
        for (AutoCloseable closeable : closeables) {
            try {
                closeable.close();
            } catch (Exception e) {
                SodiumClientMod.logger().error("Error closing baking data closeables", e);
            }
        }
        closeables.clear();
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean requiresIndexing(MultiPhaseParametersAccessor multiPhaseParameters) {
        // instanced: opaque and additive with depth write off
        // index buffer: everything else
        String transparencyName = multiPhaseParameters.getTransparency().toString();
        return !transparencyName.equals("no_transparency")
                && !(transparencyName.equals("additive_transparency") && multiPhaseParameters.getWriteMaskState().equals(RenderPhase.COLOR_MASK));
    }

}
