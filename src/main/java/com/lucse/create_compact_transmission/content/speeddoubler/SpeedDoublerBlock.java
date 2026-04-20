package com.lucse.create_compact_transmission.content.speeddoubler;

import com.lucse.create_compact_transmission.CCTBlockEntityTypes;
import com.simibubi.create.content.kinetics.transmission.ClutchBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class SpeedDoublerBlock extends ClutchBlock {

    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public SpeedDoublerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWER);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int power = context.getLevel().getBestNeighborSignal(context.getClickedPos());
        return super.getStateForPlacement(context)
                .setValue(POWERED, power > 0)
                .setValue(POWER, power);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
                                boolean isMoving) {
        if (worldIn.isClientSide)
            return;

        int newPower = worldIn.getBestNeighborSignal(pos);
        int oldPower = state.getValue(POWER);
        
        if (newPower != oldPower) {
            worldIn.setBlock(pos, state.setValue(POWERED, newPower > 0).setValue(POWER, newPower), 2 | 16);
            detachKinetics(worldIn, pos, true);
        }
    }

    @Override
    public BlockEntityType<? extends SplitShaftBlockEntity> getBlockEntityType() {
        return CCTBlockEntityTypes.SPEED_DOUBLER.get();
    }

}

