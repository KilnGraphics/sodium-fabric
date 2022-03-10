package me.jellysquid.mods.sodium.interop.vanilla.mixin;

import net.minecraft.client.render.RenderLayer;

// WARNING: EXTREME JANK
// FIXME: this is not a good way to know what renderlayer a BufferBuilder was created with.
public interface BufferBuilderHolder {
    RenderLayer getRenderLayer();

    void setRenderLayer(RenderLayer renderLayer);
}
