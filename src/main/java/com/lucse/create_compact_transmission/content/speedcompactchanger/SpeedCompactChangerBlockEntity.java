package com.lucse.create_compact_transmission.content.speedcompactchanger;

import com.lucse.create_compact_transmission.content.kinetics.KineticSpeedLimiter;
import com.simibubi.create.content.kinetics.transmission.ClutchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SpeedCompactChangerBlockEntity extends ClutchBlockEntity {

    public SpeedCompactChangerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (hasSource()) {
            Direction sourceFacing = getSourceFacing();
            int power = getBlockState().getValue(SpeedCompactChangerBlock.POWER);
            
            if (face == sourceFacing) {
                return 1;
            } else {
                return KineticSpeedLimiter.clampModifier(this, power * 2.0f / 15.0f);
            }
        }
        return 1;
    }

}
