package me.jellysquid.mods.sodium.interop.vanilla.vertex.formats.entity;

import me.jellysquid.mods.sodium.interop.vanilla.vertex.fallback.VertexWriterFallback;
import me.jellysquid.mods.sodium.interop.vanilla.vertex.formats.quad.QuadVertexSink;
import me.jellysquid.mods.sodium.render.vertex.buffer.VertexBufferView;
import me.jellysquid.mods.sodium.util.packed.ColorABGR;
import me.jellysquid.mods.sodium.util.packed.Normal3b;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferVertexConsumer;
import net.minecraft.client.render.VertexConsumer;

public class ModelPartQuadVertexWriterFallback extends VertexWriterFallback implements ModelPartQuadVertexSink {
    public ModelPartQuadVertexWriterFallback(VertexConsumer consumer) {
        super(consumer);
    }

    @Override
    public void writeQuad(float x, float y, float z, int color, float u, float v, int light, int overlay, int normal, int partId) {
        VertexConsumer consumer = this.consumer;

        // padding is dealt with by VertexConsumer with provided vertex format
        consumer.vertex(x, y, z);
        // 4 bytes padding
        consumer.texture(u, v);
        consumer.normal(Normal3b.unpackX(normal), Normal3b.unpackY(normal), Normal3b.unpackZ(normal));
        // 1 byte padding

        // need to manually write to the buffer for the part id
        VertexBufferView bufferView = (VertexBufferView) consumer;
        bufferView.getDirectBuffer().putInt(bufferView.getWriterPosition(), partId);
        // need to manually switch to the next vertex format element
        BufferVertexConsumer bufferConsumer = (BufferVertexConsumer) consumer;
        bufferConsumer.nextElement();

        consumer.next();
    }
}
