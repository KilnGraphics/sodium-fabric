package me.jellysquid.mods.sodium.asm;

import me.jellysquid.mods.sodium.interop.vanilla.vertex.VanillaVertexFormats;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferWriterUnsafe;
import org.lwjgl.system.MemoryUtil;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ExampleGeneratedVertexSink extends VertexBufferWriterUnsafe implements GeneratedVertexSink {

    private static final MethodHandle WRITE_VERTEX_HANDLE;

    static {
        MethodHandle m = null;
        try {
            m = createWriteVertexHandle();
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }

        WRITE_VERTEX_HANDLE = m;
    }

    private static MethodHandle createWriteVertexHandle() throws NoSuchMethodException, IllegalAccessException {
//        try {
            return MethodHandles.lookup().findVirtual(
                    ExampleGeneratedVertexSink.class,
                    "writeVertex",
                    MethodType.methodType(void.class, float.class, float.class, float.class, int.class, float.class, float.class, int.class, int.class, int.class)
            );
//        } catch (NoSuchMethodException | IllegalAccessException e) {
//            throw new IllegalStateException("Unable to locate writeVertex method for method handle", e);
//        }
    }

    public ExampleGeneratedVertexSink(VertexBufferView backingBuffer) {
        super(backingBuffer, VanillaVertexFormats.QUADS);
    }

    public void writeVertex(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal) {
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

        this.advance();
    }

    @Override
    public MethodHandle getWriteVertexHandle() {
        return WRITE_VERTEX_HANDLE;
    }

}
