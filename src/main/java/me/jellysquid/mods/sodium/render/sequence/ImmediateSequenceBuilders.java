package me.jellysquid.mods.sodium.render.sequence;

import java.nio.ByteBuffer;

import me.jellysquid.mods.sodium.opengl.types.IntType;
import net.minecraft.client.render.VertexFormat;
import org.lwjgl.system.MemoryUtil;

public class ImmediateSequenceBuilders {
    public static final SequenceBuilder QUADS = new SequenceBuilder() {
        @Override
        public void write(long pointer, int baseVertex) {
            MemoryUtil.memPutInt(pointer + 0 , baseVertex + 0);
            MemoryUtil.memPutInt(pointer + 4 , baseVertex + 1);
            MemoryUtil.memPutInt(pointer + 8 , baseVertex + 2);
            MemoryUtil.memPutInt(pointer + 12, baseVertex + 2);
            MemoryUtil.memPutInt(pointer + 16, baseVertex + 3);
            MemoryUtil.memPutInt(pointer + 20, baseVertex + 0);
        }

        @Override
        public void write(ByteBuffer buffer, int baseVertex) {
            buffer.putInt(baseVertex + 0);
            buffer.putInt(baseVertex + 1);
            buffer.putInt(baseVertex + 2);
            buffer.putInt(baseVertex + 2);
            buffer.putInt(baseVertex + 3);
            buffer.putInt(baseVertex + 0);
        }

        @Override
        public int getVerticesPerPrimitive() {
            return 4;
        }

        @Override
        public int getIndicesPerPrimitive() {
            return 6;
        }

        @Override
        public IntType getElementType() {
            return IntType.UNSIGNED_INT;
        }
    };

    public static final SequenceBuilder LINES = new SequenceBuilder() {
        @Override
        public void write(long pointer, int baseVertex) {
            MemoryUtil.memPutInt(pointer + 0 , baseVertex + 0);
            MemoryUtil.memPutInt(pointer + 4 , baseVertex + 1);
            MemoryUtil.memPutInt(pointer + 8 , baseVertex + 2);
            MemoryUtil.memPutInt(pointer + 12, baseVertex + 3);
            MemoryUtil.memPutInt(pointer + 16, baseVertex + 2);
            MemoryUtil.memPutInt(pointer + 20, baseVertex + 1);
        }

        @Override
        public void write(ByteBuffer buffer, int baseVertex) {
            buffer.putInt(baseVertex + 0);
            buffer.putInt(baseVertex + 1);
            buffer.putInt(baseVertex + 2);
            buffer.putInt(baseVertex + 3);
            buffer.putInt(baseVertex + 2);
            buffer.putInt(baseVertex + 1);
        }

        @Override
        public int getVerticesPerPrimitive() {
            return 4;
        }

        @Override
        public int getIndicesPerPrimitive() {
            return 6;
        }

        @Override
        public IntType getElementType() {
            return IntType.UNSIGNED_INT;
        }
    };

    public static final SequenceBuilder NONE = new SequenceBuilder() {
        @Override
        public void write(long pointer, int baseVertex) {
            MemoryUtil.memPutInt(pointer, baseVertex);
        }

        @Override
        public void write(ByteBuffer buffer, int baseVertex) {
            buffer.putInt(baseVertex);
        }

        @Override
        public int getVerticesPerPrimitive() {
            return 1;
        }

        @Override
        public int getIndicesPerPrimitive() {
            return 1;
        }

        @Override
        public IntType getElementType() {
            return IntType.UNSIGNED_INT;
        }
    };

    public static SequenceBuilder map(VertexFormat.DrawMode drawMode) {
        if (drawMode == VertexFormat.DrawMode.QUADS) {
            return QUADS;
        } else if (drawMode == VertexFormat.DrawMode.LINES) {
            return LINES;
        } else {
            return NONE;
        }
    }

    public SequenceBuilder[] values() {
        return new
    }
}
