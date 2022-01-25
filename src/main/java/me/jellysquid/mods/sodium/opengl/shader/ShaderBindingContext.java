package me.jellysquid.mods.sodium.opengl.shader;

import me.jellysquid.mods.sodium.opengl.shader.data.BufferBlock;
import me.jellysquid.mods.sodium.opengl.shader.uniform.Uniform;
import me.jellysquid.mods.sodium.opengl.shader.uniform.UniformFactory;

public interface ShaderBindingContext {
    <U extends Uniform> U bindUniform(String name, UniformFactory<U> factory);

    BufferBlock bindUniformBlock(String name, int bindingPoint);
}
