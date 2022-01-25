package me.jellysquid.mods.sodium.opengl.shader.data;

import org.lwjgl.opengl.*;

public enum BufferBlockType {
    UNIFORM(GL31C.GL_UNIFORM_BUFFER),
    SHADER_STORAGE(GL43C.GL_SHADER_STORAGE_BUFFER),
    ATOMIC_COUNTER(GL42C.GL_ATOMIC_COUNTER_BUFFER),
    TRANSFORM_FEEDBACK(GL30C.GL_TRANSFORM_FEEDBACK_BUFFER);
    
    public final int id;
    
    BufferBlockType(int id) {
        this.id = id;
    }
}
