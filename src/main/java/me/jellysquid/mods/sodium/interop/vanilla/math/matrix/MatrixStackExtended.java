package me.jellysquid.mods.sodium.interop.vanilla.math.matrix;

import me.jellysquid.mods.sodium.render.entity.data.InstanceBatch;

public interface MatrixStackExtended {
    InstanceBatch getBatch();

    void setBatch(InstanceBatch batch);
}
