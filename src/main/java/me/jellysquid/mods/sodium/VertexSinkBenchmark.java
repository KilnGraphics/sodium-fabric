package me.jellysquid.mods.sodium;

import me.jellysquid.mods.sodium.asm.GeneratedVertexSink;
import me.jellysquid.mods.sodium.asm.PreGenVertexSink;
import me.jellysquid.mods.sodium.opengl.attribute.VertexAttributeBinding;
import me.jellysquid.mods.sodium.render.chunk.shader.ChunkShaderBindingPoints;
import me.jellysquid.mods.sodium.render.terrain.format.TerrainMeshAttribute;
import me.jellysquid.mods.sodium.render.terrain.format.standard.TerrainVertexType;
import me.jellysquid.mods.sodium.render.vertex.GeneratedVertexSinks;
import me.jellysquid.mods.sodium.render.vertex.VertexSink;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferBuilder;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferWriterUnsafe;
import me.jellysquid.mods.sodium.render.vertex.type.BufferVertexFormat;
import me.jellysquid.mods.sodium.render.vertex.type.BufferVertexType;
import net.minecraft.client.render.VertexConsumer;
import org.openjdk.jmh.annotations.*;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class VertexSinkBenchmark {

    @State(Scope.Benchmark)
    public static class StateHolder {
        public long arg1;
        public int arg2;
        public int arg3;
        public int arg4;
        public BufferVertexType<?> bufferVertexType;
        public VertexBufferBuilder vertexBufferBuilder;
        public GeneratedVertexSink generatedSink;
        public GeneratedVertexSink preGenSink;
        public PreGenVertexSink rawSink;
        public MethodHandle generatedHandle;
        public MethodHandle preGenHandle;
        public Method resetHandle;

        @Setup(Level.Trial)
        public void initTrial() throws NoSuchMethodException {
            VertexAttributeBinding[] attributeBindings = new VertexAttributeBinding[] {
                    new VertexAttributeBinding(ChunkShaderBindingPoints.ATTRIBUTE_POSITION,
                            TerrainVertexType.VERTEX_FORMAT.getAttribute(TerrainMeshAttribute.POSITION)),
                    new VertexAttributeBinding(ChunkShaderBindingPoints.ATTRIBUTE_COLOR,
                            TerrainVertexType.VERTEX_FORMAT.getAttribute(TerrainMeshAttribute.COLOR)),
                    new VertexAttributeBinding(ChunkShaderBindingPoints.ATTRIBUTE_BLOCK_TEXTURE,
                            TerrainVertexType.VERTEX_FORMAT.getAttribute(TerrainMeshAttribute.BLOCK_TEXTURE)),
                    new VertexAttributeBinding(ChunkShaderBindingPoints.ATTRIBUTE_LIGHT_TEXTURE,
                            TerrainVertexType.VERTEX_FORMAT.getAttribute(TerrainMeshAttribute.LIGHT_TEXTURE))
            };
            bufferVertexType = new BufferVertexType<>() {
                @Override
                public VertexSink createFallbackWriter(VertexConsumer consumer) {
                    return null;
                }

                @Override
                public BufferVertexFormat getBufferVertexFormat() {
                    return TerrainVertexType.VERTEX_FORMAT;
                }
            };

            vertexBufferBuilder = new VertexBufferBuilder(bufferVertexType.getBufferVertexFormat(), 0);
            vertexBufferBuilder.ensureBufferCapacity(Integer.MAX_VALUE);

            generatedSink = GeneratedVertexSinks.create(attributeBindings, true, vertexBufferBuilder, bufferVertexType);
            generatedHandle = generatedSink.getWriteVertexHandle();

            preGenSink = new PreGenVertexSink(vertexBufferBuilder, bufferVertexType);
            preGenHandle = preGenSink.getWriteVertexHandle();

            rawSink = new PreGenVertexSink(vertexBufferBuilder, bufferVertexType);

            resetHandle = VertexBufferWriterUnsafe.class.getDeclaredMethod("onBufferStorageChanged");
            resetHandle.setAccessible(true);
        }

        @Setup(Level.Iteration)
        public void initIteration() throws InvocationTargetException, IllegalAccessException {
            Random random = new Random();
            arg1 = random.nextLong();
            arg2 = random.nextInt();
            arg3 = random.nextInt();
            arg4 = random.nextInt();

            vertexBufferBuilder.reset();
            vertexBufferBuilder.ensureBufferCapacity(Integer.MAX_VALUE);
            resetHandle.invoke(generatedSink);
            resetHandle.invoke(preGenSink);
            resetHandle.invoke(rawSink);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 30, time = 300, timeUnit = TimeUnit.MILLISECONDS)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testRaw(StateHolder stateHolder) {
        stateHolder.rawSink.writeVertex(
                stateHolder.arg1,
                stateHolder.arg2,
                stateHolder.arg3,
                stateHolder.arg4
        );
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 30, time = 300, timeUnit = TimeUnit.MILLISECONDS)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testGeneratedHandle(StateHolder stateHolder) {
        try {
            stateHolder.generatedHandle.invokeExact(
                    stateHolder.generatedSink,
                    stateHolder.arg1,
                    stateHolder.arg2,
                    stateHolder.arg3,
                    stateHolder.arg4
            );
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 5, time = 300, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 30, time = 300, timeUnit = TimeUnit.MILLISECONDS)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void testPreGenHandle(StateHolder stateHolder) {
        try {
            stateHolder.preGenHandle.invokeExact(
                    stateHolder.preGenSink,
                    stateHolder.arg1,
                    stateHolder.arg2,
                    stateHolder.arg3,
                    stateHolder.arg4
            );
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
