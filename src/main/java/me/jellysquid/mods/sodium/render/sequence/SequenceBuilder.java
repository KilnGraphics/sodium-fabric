package me.jellysquid.mods.sodium.render.sequence;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import me.jellysquid.mods.sodium.opengl.types.IntType;

public interface SequenceBuilder {
    void write(long pointer, int baseVertex);
    void write(ByteBuffer buffer, int baseVertex);

    int getVerticesPerPrimitive();
    int getIndicesPerPrimitive();
    IntType getElementType();
}
