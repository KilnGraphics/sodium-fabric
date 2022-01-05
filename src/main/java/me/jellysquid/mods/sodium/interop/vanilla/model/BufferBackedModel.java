package me.jellysquid.mods.sodium.interop.vanilla.model;

import me.jellysquid.mods.sodium.opengl.array.VertexArrayBuffer;

public interface BufferBackedModel {
    VertexArrayBuffer getVertexBuffer();

    int getVertexCount();

    float[] getPrimitivePositions();

    int[] getPrimitivePartIds();
}
