package me.jellysquid.mods.sodium.render.sequence;

import me.jellysquid.mods.sodium.opengl.types.IntType;

import java.nio.ByteBuffer;

public interface SequenceBuilder {
    void write(long pointer, int baseVertex);
    void write(ByteBuffer buffer, int baseVertex);

    int getVerticesPerPrimitive();
    int getIndicesPerPrimitive();
    IntType getElementType();
}
