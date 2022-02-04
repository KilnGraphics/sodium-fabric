package me.jellysquid.mods.sodium.render.vertex;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import me.jellysquid.mods.sodium.asm.AsmVertexSinkClassFactory;
import me.jellysquid.mods.sodium.asm.GeneratedVertexSink;
import me.jellysquid.mods.sodium.opengl.attribute.VertexAttributeBinding;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.render.vertex.type.BufferVertexType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class GeneratedVertexSinks {

    private static final Map<SinkDefinition, Class<GeneratedVertexSink>> VALUES = new Object2ReferenceOpenHashMap<>();

    public static GeneratedVertexSink create(VertexAttributeBinding[] attributeBindings, boolean packParams, VertexBufferView backingBuffer, BufferVertexType<?> vertexType) {
        Class<GeneratedVertexSink> sinkClass = VALUES.computeIfAbsent(new SinkDefinition(attributeBindings, packParams), sd -> {
            try {
                return AsmVertexSinkClassFactory.generateVertexSinkClass(sd.attributeBindings, sd.packParams);
            } catch (Exception e) {
                throw new RuntimeException("Error generating vertex sink class", e);
            }
        });

        // create instance of generated class
        try {
            Constructor<GeneratedVertexSink> constructor = sinkClass.getConstructor(VertexBufferView.class, BufferVertexType.class);
            return constructor.newInstance(backingBuffer, vertexType);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to create generated vertex sink instance", e);
        }
    }

    private static class SinkDefinition {
        private final VertexAttributeBinding[] attributeBindings;
        private final boolean packParams;

        private SinkDefinition(VertexAttributeBinding[] attributeBindings, boolean packParams) {
            this.attributeBindings = attributeBindings;
            this.packParams = packParams;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SinkDefinition that = (SinkDefinition) o;
            return packParams == that.packParams && Arrays.equals(attributeBindings, that.attributeBindings);
        }

        @Override
        public int hashCode() {
            int result = Boolean.hashCode(packParams);
            result = 31 * result + Arrays.hashCode(attributeBindings);
            return result;
        }
    }
}
