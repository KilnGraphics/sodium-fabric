package me.jellysquid.mods.sodium.opengl.attribute;

import java.util.Objects;

public class VertexAttributeBinding extends VertexAttribute {
    private final int index;

    public VertexAttributeBinding(int index, VertexAttribute attribute) {
        super(attribute.getFormat(), attribute.getSize(), attribute.getCount(), attribute.isNormalized(), attribute.getOffset(), attribute.isIntType());

        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        VertexAttributeBinding that = (VertexAttributeBinding) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), index);
    }
}
