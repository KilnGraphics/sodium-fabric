package me.jellysquid.mods.sodium.interop.vanilla.mixin;

import net.minecraft.client.render.Shader;

import java.util.function.Supplier;

public interface RenderPhaseShaderAccessor {
    Supplier<Shader> getShader();
}
