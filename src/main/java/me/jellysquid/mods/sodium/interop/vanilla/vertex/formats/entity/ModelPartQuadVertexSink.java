package me.jellysquid.mods.sodium.interop.vanilla.vertex.formats.entity;

import me.jellysquid.mods.sodium.interop.vanilla.math.matrix.Matrix4fExtended;
import me.jellysquid.mods.sodium.interop.vanilla.math.matrix.MatrixUtil;
import me.jellysquid.mods.sodium.render.entity.InstancedModelVertexFormats;
import me.jellysquid.mods.sodium.render.vertex.VertexSink;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.math.MatrixStack;

public interface ModelPartQuadVertexSink extends VertexSink {
    VertexFormat VERTEX_FORMAT = InstancedModelVertexFormats.DEFAULT_ENTITY_FORMAT;
    int VERTICES_PER_PRIMITIVE = 4; // TODO: move this

    /**
     * Writes a quad vertex to this sink.
     * TODO: rename to writeQuadVertex? the current name is ambiguous (should be applied to all primitive-specific sinks)
     *
     * @param x The x-position of the vertex
     * @param y The y-position of the vertex
     * @param z The z-position of the vertex
     * @param color The ABGR-packed color of the vertex
     * @param u The u-texture of the vertex
     * @param v The y-texture of the vertex
     * @param light The packed light-map coordinates of the vertex
     * @param overlay The packed overlay-map coordinates of the vertex
     * @param normal The 3-byte packed normal vector of the vertex
     * @param partId The id of the model part that this vertex falls under
     */
    void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal, int partId);

    /**
     * Writes a quad vertex to the sink, transformed by the given matrices.
     *
     * @param matrices The matrices to transform the vertex's position and normal vectors by
     */
    default void writeQuad(MatrixStack.Entry matrices, float x, float y, float z, int color, float u, float v, int light, int overlay, int normal, int partId) {
        Matrix4fExtended positionMatrix = MatrixUtil.getExtendedMatrix(matrices.getPositionMatrix());

        float x2 = positionMatrix.transformVecX(x, y, z);
        float y2 = positionMatrix.transformVecY(x, y, z);
        float z2 = positionMatrix.transformVecZ(x, y, z);

        int norm = MatrixUtil.transformPackedNormal(normal, matrices.getNormalMatrix());

        this.writeQuad(x2, y2, z2, color, u, v, light, overlay, norm, partId);
    }
}
