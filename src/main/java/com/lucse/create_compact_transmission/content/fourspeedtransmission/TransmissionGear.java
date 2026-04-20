package com.lucse.create_compact_transmission.content.fourspeedtransmission;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TransmissionGear implements StringRepresentable {
    REVERSE("reverse", "R"),
    NEUTRAL("neutral", "N"),
    FIRST("first", "1"),
    SECOND("second", "2"),
    THIRD("third", "3"),
    FOURTH("fourth", "4");

    private final String name;
    private final String displayName;

    TransmissionGear(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    @Override
    @NotNull
    public String getSerializedName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public TransmissionGear shiftUp() {
        return switch (this) {
            case REVERSE -> NEUTRAL;
            case NEUTRAL -> FIRST;
            case FIRST -> SECOND;
            case SECOND -> THIRD;
            case THIRD -> FOURTH;
            case FOURTH -> FOURTH;
        };
    }

    public TransmissionGear shiftDown() {
        return switch (this) {
            case FOURTH -> THIRD;
            case THIRD -> SECOND;
            case SECOND -> FIRST;
            case FIRST -> NEUTRAL;
            case NEUTRAL -> REVERSE;
            case REVERSE -> REVERSE;
        };
    }

    public float getDefaultMultiplier() {
        return switch (this) {
            case REVERSE -> -1.0f;
            case NEUTRAL -> 0.0f;
            case FIRST -> 0.5f;
            case SECOND -> 1.0f;
            case THIRD -> 1.8f;
            case FOURTH -> 2.5f;
        };
    }
}

