package com.lucse.create_compact_transmission.content.smartspeeddoubler;

import com.lucse.create_compact_transmission.CCTBlockEntityTypes;
import com.simibubi.create.content.kinetics.transmission.ClutchBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.ticks.TickPriority;
import net.minecraft.world.phys.BlockHitResult;

public class SmartSpeedDoublerBlock extends ClutchBlock {

    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public SmartSpeedDoublerBlock(Properties properties) {
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
        Direction direction = Direction.getNearest(
                fromPos.getX() - pos.getX(),
                fromPos.getY() - pos.getY(),
                fromPos.getZ() - pos.getZ()
        );
        Direction.Axis blockAxis = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS);
        if (direction.getAxis() == blockAxis) {
            return;
        }

        int newPower = worldIn.getBestNeighborSignal(pos);
        int oldPower = state.getValue(POWER);
        boolean needsUpdate = (newPower != oldPower);
        if (blockAxis == Direction.Axis.Y) {
            if (direction != Direction.UP && direction != Direction.DOWN) {
                needsUpdate = true;
            } else {
                needsUpdate = true;
            }
        } else {
            if (direction == Direction.UP || direction == Direction.DOWN) {
                needsUpdate = true;
            } else {
                Direction positiveDir, negativeDir;
                if (blockAxis == Direction.Axis.X) {
                    positiveDir = Direction.SOUTH;
                    negativeDir = Direction.NORTH;
                } else {
                    positiveDir = Direction.EAST;
                    negativeDir = Direction.WEST;
                }
                
                if (direction == positiveDir || direction == negativeDir) {
                    needsUpdate = true;
                }
            }
        }
        if (needsUpdate) {
            worldIn.setBlock(pos, state.setValue(POWERED, newPower > 0).setValue(POWER, newPower), 2 | 16);
            if (worldIn instanceof ServerLevel serverLevel) {
                serverLevel.scheduleTick(pos, this, 1, TickPriority.NORMAL);
            }
        }
    }
    
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        if (worldIn.isClientSide)
            return;
        detachKinetics(worldIn, pos, false);
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be instanceof com.simibubi.create.content.kinetics.base.KineticBlockEntity kbe) {
            com.simibubi.create.content.kinetics.RotationPropagator.handleAdded(worldIn, pos, kbe);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult ray) {
        if (world.isClientSide) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SmartSpeedDoublerBlockEntity smartSpeedDoubler) {
                SmartSpeedDoublerScreen.open(smartSpeedDoubler);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public BlockEntityType<? extends SplitShaftBlockEntity> getBlockEntityType() {
        return CCTBlockEntityTypes.SMART_SPEED_DOUBLER.get();
    }

}
