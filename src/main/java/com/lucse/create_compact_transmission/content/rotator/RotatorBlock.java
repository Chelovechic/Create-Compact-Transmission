package com.lucse.create_compact_transmission.content.rotator;

import com.lucse.create_compact_transmission.CCTBlockEntityTypes;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.ClutchBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.ticks.TickPriority;

public class RotatorBlock extends ClutchBlock {

    public static final EnumProperty<RotatorDirection> DIRECTION = EnumProperty.create("direction", RotatorDirection.class);

    public RotatorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(DIRECTION, RotatorDirection.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DIRECTION);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context)
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()))
                .setValue(DIRECTION, RotatorDirection.NONE);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
                                boolean isMoving) {
        if (worldIn.isClientSide)
            return;

        Direction signalDirection = Direction.getNearest(
                fromPos.getX() - pos.getX(),
                fromPos.getY() - pos.getY(),
                fromPos.getZ() - pos.getZ()
        );
        Direction.Axis blockAxis = state.getValue(AXIS);
        if (signalDirection.getAxis() == blockAxis) {
            return;
        }

        boolean hasSignal = worldIn.getBestNeighborSignal(pos) > 0;
        boolean previouslyPowered = state.getValue(POWERED);
        RotatorDirection currentDirection = state.getValue(DIRECTION);
        RotatorDirection newDirection = RotatorDirection.NONE;

        if (hasSignal) {
            newDirection = determineDirectionFromSignal(worldIn, pos, state, signalDirection);
        }

        if (hasSignal != previouslyPowered || newDirection != currentDirection) {
            worldIn.setBlock(pos, state.setValue(POWERED, hasSignal).setValue(DIRECTION, newDirection), 2 | 16);
            detachKinetics(worldIn, pos, true);
        }
    }

    private RotatorDirection determineDirectionFromSignal(Level worldIn, BlockPos pos, BlockState state, Direction signalDirection) {
        Direction.Axis blockAxis = state.getValue(AXIS);
        if (blockAxis == Direction.Axis.Y) {
            int upSignal = worldIn.getSignal(pos.relative(Direction.UP), Direction.UP);
            int downSignal = worldIn.getSignal(pos.relative(Direction.DOWN), Direction.DOWN);
            if (signalDirection == Direction.UP || upSignal > 0) {
                if (downSignal == 0) {
                    return RotatorDirection.RIGHT;
                }
            }
            if (signalDirection == Direction.DOWN || downSignal > 0) {
                if (upSignal == 0) {
                    return RotatorDirection.LEFT;
                }
            }
            if (signalDirection == Direction.EAST || signalDirection == Direction.SOUTH) {
                return RotatorDirection.RIGHT;
            }
            if (signalDirection == Direction.WEST || signalDirection == Direction.NORTH) {
                return RotatorDirection.LEFT;
            }
            int eastSignal = worldIn.getSignal(pos.relative(Direction.EAST), Direction.EAST);
            int westSignal = worldIn.getSignal(pos.relative(Direction.WEST), Direction.WEST);
            int southSignal = worldIn.getSignal(pos.relative(Direction.SOUTH), Direction.SOUTH);
            int northSignal = worldIn.getSignal(pos.relative(Direction.NORTH), Direction.NORTH);
            if (upSignal > 0 && downSignal == 0) {
                return RotatorDirection.RIGHT;
            }
            if (downSignal > 0 && upSignal == 0) {
                return RotatorDirection.LEFT;
            }
            if ((eastSignal > 0 || southSignal > 0) && westSignal == 0 && northSignal == 0) {
                return RotatorDirection.RIGHT;
            }
            if ((westSignal > 0 || northSignal > 0) && eastSignal == 0 && southSignal == 0) {
                return RotatorDirection.LEFT;
            }
            if (downSignal > 0) {
                return RotatorDirection.LEFT;
            }
            if (upSignal > 0) {
                return RotatorDirection.RIGHT;
            }
            if (westSignal > 0 || northSignal > 0) {
                return RotatorDirection.LEFT;
            }
            if (eastSignal > 0 || southSignal > 0) {
                return RotatorDirection.RIGHT;
            }
            RotatorDirection current = state.getValue(DIRECTION);
            if (current != RotatorDirection.NONE) {
                return current;
            }
            return RotatorDirection.RIGHT;
        }
        Direction positiveDir, negativeDir;
        if (blockAxis == Direction.Axis.X) {
            positiveDir = Direction.SOUTH;
            negativeDir = Direction.NORTH;
        } else {
            positiveDir = Direction.EAST;
            negativeDir = Direction.WEST;
        }
        int positiveSignal = worldIn.getSignal(pos.relative(positiveDir), positiveDir);
        int negativeSignal = worldIn.getSignal(pos.relative(negativeDir), negativeDir);
        int upSignal = worldIn.getSignal(pos.relative(Direction.UP), Direction.UP);
        int downSignal = worldIn.getSignal(pos.relative(Direction.DOWN), Direction.DOWN);
        if (negativeSignal > 0) {
            return RotatorDirection.LEFT;
        }
        if (downSignal > 0) {
            return RotatorDirection.LEFT;
        }
        if (upSignal > 0) {
            return RotatorDirection.RIGHT;
        }
        if (positiveSignal > 0) {
            return RotatorDirection.RIGHT;
        }
        if (signalDirection == positiveDir) {
            return RotatorDirection.RIGHT;
        }
        if (signalDirection == negativeDir) {
            return RotatorDirection.LEFT;
        }
        if (signalDirection == Direction.UP) {
            return RotatorDirection.RIGHT;
        }
        if (signalDirection == Direction.DOWN) {
            return RotatorDirection.LEFT;
        }
        RotatorDirection current = state.getValue(DIRECTION);
        if (current != RotatorDirection.NONE) {
            return current;
        }
        return RotatorDirection.RIGHT;
    }

    @Override
    public BlockEntityType<? extends SplitShaftBlockEntity> getBlockEntityType() {
        return CCTBlockEntityTypes.ROTATOR.get();
    }

    public void detachKinetics(Level worldIn, BlockPos pos, boolean reAttachNextTick) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be == null || !(be instanceof KineticBlockEntity))
            return;
        RotationPropagator.handleRemoved(worldIn, pos, (KineticBlockEntity) be);
        if (reAttachNextTick && worldIn instanceof ServerLevel serverLevel)
            serverLevel.scheduleTick(pos, this, 1, TickPriority.EXTREMELY_HIGH);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be == null || !(be instanceof KineticBlockEntity kte))
            return;
        RotationPropagator.handleAdded(worldIn, pos, kte);
    }

}

