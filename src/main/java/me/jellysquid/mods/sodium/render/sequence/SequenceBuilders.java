package me.jellysquid.mods.sodium.render.sequence;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

import me.jellysquid.mods.sodium.SodiumClientMod;
import me.jellysquid.mods.sodium.asm.AsmSequenceBuilderFactory;
import me.jellysquid.mods.sodium.opengl.types.IntType;

import java.util.Map;

import net.minecraft.client.render.VertexFormat;

public class SequenceBuilders {

    private static final Map<SequenceDefinition, SequenceBuilder> VALUES = new Object2ReferenceOpenHashMap<>();

    public static SequenceBuilder getOrCreate(IntType elementType, int... pattern) {
        return VALUES.computeIfAbsent(new SequenceDefinition(pattern, elementType), sd -> {
            try {
                return AsmSequenceBuilderFactory.generateSequenceBuilder(sd.pattern, sd.elementType);
            } catch (Exception e) {
                SodiumClientMod.logger().error("Error generating sequence builder", e);
            }
            return null;
        });
    }

    private static final int[] QUADS_INDEX_SEQUENCE = new int[] { 0, 1, 2, 2, 3, 0 };
    private static final int[] LINES_INDEX_SEQUENCE = new int[] { 0, 1, 2, 3, 2, 0 };

    public static SequenceBuilder map(VertexFormat.DrawMode drawMode, IntType elementType) {
        return switch (drawMode) {
            case QUADS -> getOrCreate(elementType, QUADS_INDEX_SEQUENCE);
            case LINES -> getOrCreate(elementType, LINES_INDEX_SEQUENCE);
            // default topology has index sequences that count up from 0 to the amount of vertices they have
            default -> getOrCreate(elementType, IntStream.range(0, drawMode.vertexCount - 1).toArray());
        };
    }

    private static class SequenceDefinition {
        private final int[] pattern;
        private final IntType elementType;

        private SequenceDefinition(int[] pattern, IntType elementType) {
            this.pattern = pattern;
            this.elementType = elementType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SequenceDefinition that = (SequenceDefinition) o;
            return Arrays.equals(pattern, that.pattern) && elementType == that.elementType;
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(elementType);
            result = 31 * result + Arrays.hashCode(pattern);
            return result;
        }
    }

}
