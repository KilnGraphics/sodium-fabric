package me.jellysquid.mods.sodium.opengl.shader.data;

import me.jellysquid.mods.sodium.opengl.buffer.Buffer;
import me.jellysquid.mods.sodium.opengl.buffer.BufferImpl;
import org.lwjgl.opengl.GL32C;

public class BufferBlock {
    private final BufferBlockType bufferBlockType;
    private final int binding;

    public BufferBlock(BufferBlockType bufferBlockType, int blockBinding) {
        this.bufferBlockType = bufferBlockType;
        this.binding = blockBinding;
    }

    public void bindBuffer(Buffer buffer) {
        GL32C.glBindBufferBase(bufferBlockType.id, this.binding, ((BufferImpl) buffer).handle());
    }

    public void bindBuffer(Buffer buffer, int offset, int length) {
        GL32C.glBindBufferRange(bufferBlockType.id, this.binding, ((BufferImpl) buffer).handle(), offset, length);
    }
}
