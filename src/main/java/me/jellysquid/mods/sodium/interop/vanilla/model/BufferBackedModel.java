package me.jellysquid.mods.sodium.interop.vanilla.model;

import me.jellysquid.mods.sodium.opengl.buffer.Buffer;

public interface BufferBackedModel {
    Buffer getVertexBuffer();

    int getVertexCount();

    float[] getPrimitivePositions();

    int[] getPrimitivePartIds();
}
