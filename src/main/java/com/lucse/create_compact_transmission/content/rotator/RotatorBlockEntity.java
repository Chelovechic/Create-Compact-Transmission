package com.lucse.create_compact_transmission.content.rotator;

import com.simibubi.create.content.kinetics.transmission.ClutchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RotatorBlockEntity extends ClutchBlockEntity {

    public RotatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (!hasSource()) {
            return 1;
        }
        
        Direction sourceFacing = getSourceFacing();
        RotatorDirection direction = getBlockState().getValue(RotatorBlock.DIRECTION);
        
        if (face == sourceFacing) {
            return 1;
        } else {
            if (direction == RotatorDirection.NONE) {
                return 0;
            }
            if (direction == RotatorDirection.RIGHT) {
                return 1;
            }
            if (direction == RotatorDirection.LEFT) {
                return -1;
            }
        }
        
        return 1;
    }

}

