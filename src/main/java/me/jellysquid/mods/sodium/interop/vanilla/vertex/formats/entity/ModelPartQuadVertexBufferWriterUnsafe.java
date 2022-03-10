package me.jellysquid.mods.sodium.interop.vanilla.vertex.formats.entity;

import me.jellysquid.mods.sodium.interop.vanilla.vertex.VanillaVertexFormats;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferWriterUnsafe;
import org.lwjgl.system.MemoryUtil;

public class ModelPartQuadVertexBufferWriterUnsafe extends VertexBufferWriterUnsafe implements ModelPartQuadVertexSink {
    public ModelPartQuadVertexBufferWriterUnsafe(VertexBufferView backingBuffer) {
        super(backingBuffer, VanillaVertexFormats.QUADS);
    }

    @Override
    public void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal, int partId) {
        long i = this.writePointer;

        MemoryUtil.memPutFloat(i, x);
        MemoryUtil.memPutFloat(i + 4, y);
        MemoryUtil.memPutFloat(i + 8, z);
        // 4 bytes padding
        MemoryUtil.memPutFloat(i + 16, u);
        MemoryUtil.memPutFloat(i + 20, v);
        MemoryUtil.memPutInt(i + 24, normal);
        // 1 byte padding
        MemoryUtil.memPutInt(i + 28, partId);

        this.advance();
    }
}
