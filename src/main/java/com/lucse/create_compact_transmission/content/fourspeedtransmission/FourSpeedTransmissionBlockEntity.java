package com.lucse.create_compact_transmission.content.fourspeedtransmission;

import com.lucse.create_compact_transmission.content.kinetics.KineticSpeedLimiter;
import com.simibubi.create.content.kinetics.transmission.ClutchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FourSpeedTransmissionBlockEntity extends ClutchBlockEntity {

    public int gearReverseRatio = -100;
    public int gear1Ratio = 50;
    public int gear2Ratio = 100;
    public int gear3Ratio = 180;
    public int gear4Ratio = 250;

    public FourSpeedTransmissionBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            Direction sourceFacing = getSourceFacing();
            TransmissionGear gear = getBlockState().getValue(FourSpeedTransmissionBlock.GEAR);
            
            if (face == sourceFacing) {
                return 1;
            } else {
                return KineticSpeedLimiter.clampModifier(this, switch (gear) {
                    case REVERSE -> gearReverseRatio / 100.0f;
                    case NEUTRAL -> 0.0f;
                    case FIRST -> gear1Ratio / 100.0f;
                    case SECOND -> gear2Ratio / 100.0f;
                    case THIRD -> gear3Ratio / 100.0f;
                    case FOURTH -> gear4Ratio / 100.0f;
                });
            }
        }
        return 1;
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (compound.contains("gearR"))
            gearReverseRatio = compound.getInt("gearR");
        if (compound.contains("gear1"))
            gear1Ratio = compound.getInt("gear1");
        if (compound.contains("gear2"))
            gear2Ratio = compound.getInt("gear2");
        if (compound.contains("gear3"))
            gear3Ratio = compound.getInt("gear3");
        if (compound.contains("gear4"))
            gear4Ratio = compound.getInt("gear4");
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("gearR", gearReverseRatio);
        compound.putInt("gear1", gear1Ratio);
        compound.putInt("gear2", gear2Ratio);
        compound.putInt("gear3", gear3Ratio);
        compound.putInt("gear4", gear4Ratio);
    }

}
