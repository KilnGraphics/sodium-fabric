package me.jellysquid.mods.sodium.render.entity.buffer;

import me.jellysquid.mods.sodium.opengl.device.RenderDevice;
import me.jellysquid.mods.sodium.render.stream.MappedStreamingBuffer;
import me.jellysquid.mods.sodium.render.stream.StreamingBuffer;

public class RenderInstancedStorage {
    private static final int PART_BUFFER_SIZE = 9175040; // 8.75 MiB
    private static final int MODEL_BUFFER_SIZE = 524288; // 512 KiB
    private static final int TRANSLUCENT_EBO_SIZE = 1048576; // 1 MiB

    public final StreamingBuffer partBuffer;
    public final StreamingBuffer modelBuffer;
    public final StreamingBuffer translucentElementBuffer;

    public RenderInstancedStorage(RenderDevice renderDevice) {
        this.partBuffer = new MappedStreamingBuffer(renderDevice, PART_BUFFER_SIZE);
        this.modelBuffer = new MappedStreamingBuffer(renderDevice, MODEL_BUFFER_SIZE);
        this.translucentElementBuffer = new MappedStreamingBuffer(renderDevice, TRANSLUCENT_EBO_SIZE);

    }

}
