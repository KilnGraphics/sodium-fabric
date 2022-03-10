package me.jellysquid.mods.sodium.interop.vanilla.vertex.formats.entity;

import me.jellysquid.mods.sodium.interop.vanilla.vertex.VanillaVertexFormats;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferWriterNio;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;

public class ModelPartQuadVertexBufferWriterNio extends VertexBufferWriterNio implements ModelPartQuadVertexSink {
    public ModelPartQuadVertexBufferWriterNio(VertexBufferView backingBuffer) {
        super(backingBuffer, VanillaVertexFormats.QUADS);
    }

    @Override
    public void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal, int partId) {
        int i = this.writeOffset;

        ByteBuffer buf = this.byteBuffer;
        buf.putFloat(i, x);
        buf.putFloat(i + 4, y);
        buf.putFloat(i + 8, z);
        // 4 bytes padding
        buf.putFloat(i + 16, u);
        buf.putFloat(i + 20, v);
        buf.putInt(i + 24, normal);
        // 1 byte padding
        buf.putInt(i + 28, partId);

        this.advance();
    }
}
