package me.jellysquid.mods.sodium.asm;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import me.jellysquid.mods.sodium.SodiumClientMod;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferWriterUnsafe;
import me.jellysquid.mods.sodium.render.vertex.type.BufferVertexType;
import org.lwjgl.system.MemoryUtil;

public class PreGenVertexSink extends VertexBufferWriterUnsafe implements GeneratedVertexSink {
    private static final MethodHandle WRITE_VERTEX_HANDLE;

    public PreGenVertexSink(VertexBufferView backingBuffer, BufferVertexType<?> vertexType) {
        super(backingBuffer, vertexType);
    }

    public void writeVertex(long var1, int var3, int var4, int var5) {
        long var6 = this.writePointer;
        MemoryUtil.memPutLong(var6, var1);
        MemoryUtil.memPutInt(var6 + 8L, var3);
        MemoryUtil.memPutInt(var6 + 12L, var4);
        MemoryUtil.memPutInt(var6 + 16L, var5);
        this.advance();
    }

    public MethodHandle getWriteVertexHandle() {
        return WRITE_VERTEX_HANDLE;
    }

    static {
        MethodHandle var0 = null;

        try {
            MethodType var1 = MethodType.methodType(Void.TYPE, new Class[]{Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE});
            var0 = MethodHandles.lookup().findVirtual(PreGenVertexSink.class, "writeVertex", var1);
            var0 = var0.asType(var0.type().changeParameterType(0, GeneratedVertexSink.class));
        } catch (IllegalAccessException | NoSuchMethodException var2) {
            SodiumClientMod.logger().error("Unable to locate method writeVertex for method handle", var2);
        }

        WRITE_VERTEX_HANDLE = var0;
    }
}
