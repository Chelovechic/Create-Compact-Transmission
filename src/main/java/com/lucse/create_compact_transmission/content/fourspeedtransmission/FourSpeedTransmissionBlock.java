package com.lucse.create_compact_transmission.content.fourspeedtransmission;

import com.lucse.create_compact_transmission.CCTBlockEntityTypes;
import com.lucse.create_compact_transmission.CCTItems;
import com.lucse.create_compact_transmission.CCTSoundEvents;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.ClutchBlock;
import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.ticks.TickPriority;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class FourSpeedTransmissionBlock extends ClutchBlock {

    public static final EnumProperty<TransmissionGear> GEAR = EnumProperty.create("gear", TransmissionGear.class);

    public FourSpeedTransmissionBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false).setValue(GEAR, TransmissionGear.NEUTRAL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(GEAR);
        super.createBlockStateDefinition(builder);
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
        Direction.Axis blockAxis = state.getValue(AXIS);
        if (direction.getAxis() == blockAxis) {
            return;
        }

        boolean hasSignal = worldIn.getBestNeighborSignal(pos) >= 15;
        boolean previouslyPowered = state.getValue(POWERED);

        if (hasSignal && !previouslyPowered) {
            TransmissionGear currentGear = state.getValue(GEAR);
            TransmissionGear newGear = currentGear;
            boolean shiftUp = shouldShiftUp(worldIn, pos, state, direction);
            if (shiftUp) {
                newGear = currentGear.shiftUp();
            } else {
                newGear = currentGear.shiftDown();
            }

            if (newGear != currentGear) {
                worldIn.setBlock(pos, state.setValue(GEAR, newGear).setValue(POWERED, true), 2 | 16);
                detachKinetics(worldIn, pos, true);
                CCTSoundEvents.SHIFTER_SOUND.playOnServer(worldIn, pos, 0.5f, 1.0f);
            } else {
                worldIn.setBlock(pos, state.setValue(POWERED, true), 2 | 16);
            }
        } else if (!hasSignal && previouslyPowered) {
            worldIn.setBlock(pos, state.setValue(POWERED, false), 2 | 16);
        }
    }

    private boolean shouldShiftUp(Level worldIn, BlockPos pos, BlockState state, Direction signalDirection) {
        Direction.Axis blockAxis = state.getValue(AXIS);
        if (blockAxis == Direction.Axis.Y) {
            if (signalDirection == Direction.UP) {
                return true;
            }
            if (signalDirection == Direction.DOWN) {
                return false;
            }
            if (signalDirection == Direction.EAST || signalDirection == Direction.SOUTH) {
                return true;
            }
            if (signalDirection == Direction.WEST || signalDirection == Direction.NORTH) {
                return false;
            }
            int upSignal = worldIn.getSignal(pos.relative(Direction.UP), Direction.UP);
            int downSignal = worldIn.getSignal(pos.relative(Direction.DOWN), Direction.DOWN);
            int eastSignal = worldIn.getSignal(pos.relative(Direction.EAST), Direction.EAST);
            int westSignal = worldIn.getSignal(pos.relative(Direction.WEST), Direction.WEST);
            int southSignal = worldIn.getSignal(pos.relative(Direction.SOUTH), Direction.SOUTH);
            int northSignal = worldIn.getSignal(pos.relative(Direction.NORTH), Direction.NORTH);
            if (upSignal >= 15 && downSignal < 15) {
                return true;
            }
            if (downSignal >= 15 && upSignal < 15) {
                return false;
            }
            if ((eastSignal >= 15 || southSignal >= 15) && westSignal < 15 && northSignal < 15) {
                return true;
            }
            if ((westSignal >= 15 || northSignal >= 15) && eastSignal < 15 && southSignal < 15) {
                return false;
            }
            if (downSignal >= 15) {
                return false;
            }
            if (upSignal >= 15) {
                return true;
            }
            if (westSignal >= 15 || northSignal >= 15) {
                return false;
            }
            if (eastSignal >= 15 || southSignal >= 15) {
                return true;
            }
            return true;
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
        if (positiveSignal >= 15 && negativeSignal < 15 && upSignal < 15 && downSignal < 15) {
            return true;
        }
        if (upSignal >= 15 && downSignal < 15) {
            return true;
        }
        if (downSignal >= 15 && upSignal < 15) {
            return false;
        }
        if (negativeSignal >= 15 && positiveSignal < 15 && upSignal < 15 && downSignal < 15) {
            return false;
        }
        if (signalDirection == positiveDir) {
            return true;
        }
        if (signalDirection == negativeDir) {
            return false;
        }
        if (signalDirection == Direction.UP) {
            return true;
        }
        if (signalDirection == Direction.DOWN) {
            return false;
        }
        return true;
    }

    @Override
    public void detachKinetics(Level worldIn, BlockPos pos, boolean reAttachNextTick) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (be == null || !(be instanceof KineticBlockEntity))
            return;
        RotationPropagator.handleRemoved(worldIn, pos, (KineticBlockEntity) be);
        if (reAttachNextTick && worldIn instanceof ServerLevel serverLevel)
            serverLevel.scheduleTick(pos, this, 1, TickPriority.EXTREMELY_HIGH);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult ray) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean hasGearboxSetting = heldItem.is(CCTItems.GEARBOX_SETTING.get());
        boolean isCreative = player.canUseGameMasterBlocks();

        if (!hasGearboxSetting && !isCreative) {
            return InteractionResult.PASS;
        }

        if (world.isClientSide) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof FourSpeedTransmissionBlockEntity transmission) {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> FourSpeedTransmissionScreen.open(transmission));
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntityType<? extends SplitShaftBlockEntity> getBlockEntityType() {
        return CCTBlockEntityTypes.FOUR_SPEED_TRANSMISSION.get();
    }

}

