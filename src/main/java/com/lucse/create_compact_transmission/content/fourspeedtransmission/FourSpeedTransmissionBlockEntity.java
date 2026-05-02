package com.lucse.create_compact_transmission.content.fourspeedtransmission;

import com.lucse.create_compact_transmission.Config;
import com.lucse.create_compact_transmission.content.kinetics.KineticSpeedLimiter;
import com.simibubi.create.content.kinetics.transmission.ClutchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

public class FourSpeedTransmissionBlockEntity extends ClutchBlockEntity {

    public static final int MAX_GEARS = 6;
    public static final int DEFAULT_GEAR_COUNT = 4;

    public static int minRatio() {
        return -Config.FOUR_KPP_MAX.get();
    }

    public static int maxRatio() {
        return Config.FOUR_KPP_MAX.get();
    }

    private int gearCount = DEFAULT_GEAR_COUNT;
    private final int[] gearRatios = createDefaultRatios();

    public FourSpeedTransmissionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            Direction sourceFacing = getSourceFacing();
            TransmissionGear gear = getActiveGear();
            
            if (face == sourceFacing) {
                return 1;
            } else {
                return KineticSpeedLimiter.clampModifier(this, gearRatios[gear.getActiveIndex()] / 100.0f);
            }
        }
        return 1;
    }

    public int getGearCount() {
        return gearCount;
    }

    public int getRatioForGear(int index) {
        return gearRatios[Mth.clamp(index, 0, MAX_GEARS - 1)];
    }

    public int[] getGearRatiosCopy() {
        return Arrays.copyOf(gearRatios, gearRatios.length);
    }

    public void applyConfiguration(int newGearCount, int[] ratios) {
        gearCount = sanitizeGearCount(newGearCount);
        for (int i = 0; i < MAX_GEARS; i++) {
            int value = ratios != null && i < ratios.length ? ratios[i] : defaultRatio(i);
            gearRatios[i] = sanitizeRatio(value);
        }
        clampDisplayedGear();
        setChanged();
        sendData();
    }

    public TransmissionGear getActiveGear() {
        TransmissionGear gear = getBlockState().getValue(FourSpeedTransmissionBlock.GEAR);
        return gear.clampToCount(gearCount);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (compound.contains("GearRatios")) {
            int[] storedRatios = compound.getIntArray("GearRatios");
            gearCount = sanitizeGearCount(compound.getInt("GearCount"));
            for (int i = 0; i < MAX_GEARS; i++) {
                gearRatios[i] = sanitizeRatio(i < storedRatios.length ? storedRatios[i] : defaultRatio(i));
            }
            return;
        }

        gearCount = DEFAULT_GEAR_COUNT;
        gearRatios[0] = sanitizeRatio(compound.contains("gearR") ? compound.getInt("gearR") : defaultRatio(0));
        gearRatios[1] = sanitizeRatio(compound.contains("gear1") ? compound.getInt("gear1") : defaultRatio(1));
        gearRatios[2] = sanitizeRatio(compound.contains("gear2") ? compound.getInt("gear2") : defaultRatio(2));
        gearRatios[3] = sanitizeRatio(compound.contains("gear3") ? compound.getInt("gear3") : defaultRatio(3));
        gearRatios[4] = sanitizeRatio(compound.contains("gear4") ? compound.getInt("gear4") : defaultRatio(4));
        gearRatios[5] = defaultRatio(5);
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("GearCount", gearCount);
        compound.putIntArray("GearRatios", gearRatios);
    }

    private void clampDisplayedGear() {
        if (level == null) {
            return;
        }

        BlockState state = getBlockState();
        TransmissionGear clampedGear = state.getValue(FourSpeedTransmissionBlock.GEAR).clampToCount(gearCount);
        if (clampedGear != state.getValue(FourSpeedTransmissionBlock.GEAR)) {
            level.setBlock(worldPosition, state.setValue(FourSpeedTransmissionBlock.GEAR, clampedGear), 2);
        }
    }

    public static int sanitizeGearCount(int value) {
        return Mth.clamp(value, 1, MAX_GEARS);
    }

    public static int sanitizeRatio(int value) {
        return Mth.clamp(value, minRatio(), maxRatio());
    }

    public static int defaultRatio(int index) {
        return switch (Mth.clamp(index, 0, MAX_GEARS - 1)) {
            case 0 -> -100;
            case 1 -> 50;
            case 2 -> 100;
            case 3 -> 180;
            case 4 -> 250;
            case 5 -> 320;
            default -> 100;
        };
    }

    private static int[] createDefaultRatios() {
        int[] defaults = new int[MAX_GEARS];
        for (int i = 0; i < MAX_GEARS; i++) {
            defaults[i] = defaultRatio(i);
        }
        return defaults;
    }
}
