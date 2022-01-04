package me.jellysquid.mods.sodium.render.entity.renderer;

import me.jellysquid.mods.sodium.opengl.device.RenderDevice;
import me.jellysquid.mods.sodium.render.entity.data.ModelBakingData;

public interface EntityRenderer {

    void render(RenderDevice device, ModelBakingData modelBakingData);

    void delete();
}
