package me.jellysquid.mods.sodium.interop.vanilla.mixin;

import me.jellysquid.mods.sodium.render.entity.data.InstanceBatch;

public interface MatrixStackHolder {
    InstanceBatch getBatch();

    void setBatch(InstanceBatch batch);
}
