package com.lucse.create_compact_transmission.content.compactspeedregulator;

import com.lucse.create_compact_transmission.CCTBlockEntityTypes;
import com.simibubi.create.content.kinetics.transmission.ClutchBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CompactSpeedRegulatorBlock extends ClutchBlock {

    public CompactSpeedRegulatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends SplitShaftBlockEntity> getBlockEntityType() {
        return CCTBlockEntityTypes.COMPACT_SPEED_REGULATOR.get();
    }

}

