package me.jellysquid.mods.sodium.interop.vanilla.vertex.transformers;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import me.jellysquid.mods.sodium.interop.vanilla.vertex.formats.entity.ModelPartQuadVertexSink;

public class TrackingModelPartQuadVertexSink extends AbstractVertexTransformer<ModelPartQuadVertexSink> implements ModelPartQuadVertexSink {

    private final FloatArrayList quadPositions;
    private final IntArrayList quadPartIds;
    private float[] cachedQuadPositions;
    private int[] cachedQuadPartIds;

    private final float[] currentQuadVertexPositions;
    private int currentArrayIdx; // current index in the quad vertex positions array

    protected TrackingModelPartQuadVertexSink(ModelPartQuadVertexSink delegate, int initialSize) {
        super(delegate);
        this.quadPositions = new FloatArrayList(initialSize);
        this.quadPartIds = new IntArrayList(initialSize);
        this.currentQuadVertexPositions = new float[VERTICES_PER_PRIMITIVE * 3]; // 3 position axis per vertex
    }

    @Override
    public void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal, int partId) {

        // Keep track of the vertex positions with an on-CPU buffer
        currentQuadVertexPositions[currentArrayIdx++] = x;
        currentQuadVertexPositions[currentArrayIdx++] = y;
        currentQuadVertexPositions[currentArrayIdx++] = z;

        if (currentArrayIdx >= currentQuadVertexPositions.length) {
            // current quad has finished, calculate and reset
            currentArrayIdx = 0;

            // average vertex positions in primitive
            float totalX = 0.0f;
            float totalY = 0.0f;
            float totalZ = 0.0f;

            for (int vert = 0; vert < VERTICES_PER_PRIMITIVE; vert++) {
                int startingPos = vert * 3; // 3 position axis per vertex
                totalX += currentQuadVertexPositions[startingPos];
                totalY += currentQuadVertexPositions[startingPos + 1];
                totalZ += currentQuadVertexPositions[startingPos + 2];
            }

            quadPositions.add(totalX / VERTICES_PER_PRIMITIVE);
            quadPositions.add(totalY / VERTICES_PER_PRIMITIVE);
            quadPositions.add(totalZ / VERTICES_PER_PRIMITIVE);

            quadPartIds.add(partId);
        }

        this.delegate.writeQuad(x, y, z, color, u, v, light, overlay, normal, partId);
    }

    @Override
    public void ensureCapacity(int count) {
        super.ensureCapacity(count);
        this.quadPositions.ensureCapacity(count);
        this.quadPartIds.ensureCapacity(count);
        // is it necessary to null these?
        this.cachedQuadPositions = null;
        this.cachedQuadPartIds = null;
    }

    @Override
    public void flush() {
        super.flush();
        this.cachedQuadPositions = this.quadPositions.toFloatArray();
        this.cachedQuadPartIds = this.quadPartIds.toIntArray();
        this.quadPositions.clear();
        this.quadPartIds.clear();
        if (currentArrayIdx > 0) {
            throw new IllegalStateException("Flushed before quad finished, current array index is " + currentArrayIdx);
        }
    }

    public float[] getQuadPositions() {
        return cachedQuadPositions;
    }

    public int[] getQuadPartIds() {
        return cachedQuadPartIds;
    }
}
