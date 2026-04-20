package com.lucse.create_compact_transmission.content.rotator;

import net.minecraft.util.StringRepresentable;

public enum RotatorDirection implements StringRepresentable {
    NONE("none"),
    LEFT("left"),
    RIGHT("right");

    private final String name;

    RotatorDirection(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}

