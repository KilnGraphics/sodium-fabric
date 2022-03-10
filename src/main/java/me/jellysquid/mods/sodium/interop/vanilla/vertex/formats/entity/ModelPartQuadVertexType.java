package me.jellysquid.mods.sodium.interop.vanilla.vertex.formats.entity;

import me.jellysquid.mods.sodium.interop.vanilla.vertex.VanillaVertexType;
import me.jellysquid.mods.sodium.interop.vanilla.vertex.formats.quad.QuadVertexSink;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.render.vertex.type.BlittableVertexType;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexFormat;

public class ModelPartQuadVertexType implements VanillaVertexType<ModelPartQuadVertexSink>, BlittableVertexType<ModelPartQuadVertexSink> {

    @Override
    public ModelPartQuadVertexSink createFallbackWriter(VertexConsumer consumer) {
        return new ModelPartQuadVertexWriterFallback(consumer);
    }

    @Override
    public ModelPartQuadVertexSink createBufferWriter(VertexBufferView buffer, boolean direct) {
        return direct ? new ModelPartQuadVertexBufferWriterUnsafe(buffer) : new ModelPartQuadVertexBufferWriterNio(buffer);
    }

    @Override
    public VertexFormat getVertexFormat() {
        return QuadVertexSink.VERTEX_FORMAT;
    }

    @Override
    public BlittableVertexType<ModelPartQuadVertexSink> asBlittable() {
        return this;
    }
}