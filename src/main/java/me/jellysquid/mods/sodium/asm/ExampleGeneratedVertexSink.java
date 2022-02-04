package me.jellysquid.mods.sodium.asm;

import me.jellysquid.mods.sodium.SodiumClientMod;
import me.jellysquid.mods.sodium.interop.vanilla.vertex.VanillaVertexFormats;
import me.jellysquid.mods.sodium.opengl.attribute.VertexFormat;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferWriterUnsafe;
import me.jellysquid.mods.sodium.render.vertex.type.BufferVertexType;
import org.lwjgl.system.MemoryUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ExampleGeneratedVertexSink extends VertexBufferWriterUnsafe implements GeneratedVertexSink {

    private static final MethodHandle WRITE_VERTEX_HANDLE;

    static {
        MethodHandle m = null;
        try {
            MethodType methodType = MethodType.methodType(void.class, new Class[] {float.class, float.class, float.class, int.class, float.class, float.class, int.class, int.class, int.class});
            m = MethodHandles.lookup().findVirtual(ExampleGeneratedVertexSink.class, "writeVertex", methodType);
            m = m.asType(m.type().changeParameterType(0, GeneratedVertexSink.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            SodiumClientMod.logger().error("Unable to locate method writeVertex for method handle", e);
        }
        WRITE_VERTEX_HANDLE = m;
    }

    public ExampleGeneratedVertexSink(VertexBufferView backingBuffer, BufferVertexType<?> vertexType) {
        super(backingBuffer, vertexType);
    }

    public void writeVertex(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal, int test) {
        long i = this.writePointer;

        MemoryUtil.memPutFloat(i, x);
        MemoryUtil.memPutFloat(i + 4, y);
        MemoryUtil.memPutFloat(i + 8, z);
        MemoryUtil.memPutInt(i + 12, color);
        MemoryUtil.memPutFloat(i + 16, u);
        MemoryUtil.memPutFloat(i + 20, v);
        MemoryUtil.memPutInt(i + 24, overlay);
        MemoryUtil.memPutInt(i + 28, light);
        MemoryUtil.memPutInt(i + 32, normal);
        MemoryUtil.memPutInt(i + 36, test);

        this.advance();
    }

    @Override
    public MethodHandle getWriteVertexHandle() {
        return WRITE_VERTEX_HANDLE;
    }

}
