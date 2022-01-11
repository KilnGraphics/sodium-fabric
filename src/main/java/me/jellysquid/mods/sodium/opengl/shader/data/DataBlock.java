package me.jellysquid.mods.sodium.opengl.shader.data;

import me.jellysquid.mods.sodium.opengl.buffer.Buffer;
import me.jellysquid.mods.sodium.opengl.buffer.BufferImpl;
import org.lwjgl.opengl.GL32C;

public class DataBlock {
    private final BlockType blockType;
    private final int binding;

    public DataBlock(BlockType blockType, int blockBinding) {
        this.blockType = blockType;
        this.binding = blockBinding;
    }

    public void bindBuffer(Buffer buffer) {
        GL32C.glBindBufferBase(blockType.id, this.binding, ((BufferImpl) buffer).handle());
    }

    public void bindBuffer(Buffer buffer, int offset, int length) {
        GL32C.glBindBufferRange(blockType.id, this.binding, ((BufferImpl) buffer).handle(), offset, length);
    }
}
