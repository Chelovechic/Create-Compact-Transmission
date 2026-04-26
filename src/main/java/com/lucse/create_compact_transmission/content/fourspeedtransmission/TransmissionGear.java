package com.lucse.create_compact_transmission.content.fourspeedtransmission;

import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum TransmissionGear implements StringRepresentable {
    REVERSE("reverse", "1"),
    NEUTRAL("neutral", "2"),
    FIRST("first", "3"),
    SECOND("second", "4"),
    THIRD("third", "5"),
    FOURTH("fourth", "6");

    private static final TransmissionGear[] ACTIVE_GEARS = {REVERSE, NEUTRAL, FIRST, SECOND, THIRD, FOURTH};

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

    public int getActiveIndex() {
        for (int i = 0; i < ACTIVE_GEARS.length; i++) {
            if (ACTIVE_GEARS[i] == this) {
                return i;
            }
        }
        return 0;
    }

    public TransmissionGear clampToCount(int gearCount) {
        return byActiveIndex(Math.min(getActiveIndex(), Mth.clamp(gearCount, 1, ACTIVE_GEARS.length) - 1));
    }

    public TransmissionGear shift(boolean forward, int gearCount) {
        int nextIndex = getActiveIndex() + (forward ? 1 : -1);
        nextIndex = Mth.clamp(nextIndex, 0, Mth.clamp(gearCount, 1, ACTIVE_GEARS.length) - 1);
        return byActiveIndex(nextIndex);
    }

    public static TransmissionGear byActiveIndex(int index) {
        return ACTIVE_GEARS[Mth.clamp(index, 0, ACTIVE_GEARS.length - 1)];
    }

    public static int maxActiveGears() {
        return ACTIVE_GEARS.length;
    }
}
