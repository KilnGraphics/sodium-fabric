package me.jellysquid.mods.sodium.asm;

import java.lang.invoke.MethodHandle;

import me.jellysquid.mods.sodium.render.vertex.VertexSink;

public interface GeneratedVertexSink extends VertexSink {

    MethodHandle getWriteVertexHandle();
}
