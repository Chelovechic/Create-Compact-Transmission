package com.lucse.create_compact_transmission.content.fueling;

import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public final class TFMGPipeFuelingHelper {

    private static final int DEFAULT_MAX_DEPTH = 32;

    private TFMGPipeFuelingHelper() {
    }

    public static FuelTarget findAdjacentEngine(SmartBlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return null;
        }

        for (Direction direction : getConnectedDirections(blockEntity)) {
            BlockPos neighborPos = blockEntity.getBlockPos().relative(direction);
            BlockEntity neighborBlockEntity = level.getBlockEntity(neighborPos);
            if (!TFMGEngineProxy.canReceiveFuel(neighborBlockEntity)) {
                continue;
            }

            Object fuelTank = TFMGEngineProxy.getFuelTank(neighborBlockEntity);
            if (fuelTank == null) {
                continue;
            }

            return new FuelTarget(fuelTank, neighborPos);
        }

        return null;
    }

    public static IFluidHandler findFuelSource(SmartBlockEntity blockEntity, @Nullable BlockPos excludedPos) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return null;
        }

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        BlockPos startPos = blockEntity.getBlockPos();

        queue.add(startPos);
        visited.add(startPos);
        if (excludedPos != null) {
            visited.add(excludedPos);
        }

        int currentDepth = 0;
        int nodesAtCurrentDepth = 1;

        while (!queue.isEmpty() && currentDepth < DEFAULT_MAX_DEPTH) {
            BlockPos currentPos = queue.poll();
            nodesAtCurrentDepth--;

            FluidTransportBehaviour pipe = FluidPropagator.getPipe(level, currentPos);
            if (pipe != null) {
                BlockState currentState = level.getBlockState(currentPos);
                for (Direction direction : FluidPropagator.getPipeConnections(currentState, pipe)) {
                    BlockPos neighborPos = currentPos.relative(direction);
                    if (!visited.add(neighborPos)) {
                        continue;
                    }

                    BlockEntity neighborBlockEntity = level.getBlockEntity(neighborPos);
                    if (TFMGEngineProxy.isEngine(neighborBlockEntity)) {
                        continue;
                    }

                    FluidTransportBehaviour neighborPipe = FluidPropagator.getPipe(level, neighborPos);
                    if (neighborPipe != null) {
                        BlockState neighborState = level.getBlockState(neighborPos);
                        if (neighborPipe.canHaveFlowToward(neighborState, direction.getOpposite())) {
                            queue.add(neighborPos);
                        }
                        continue;
                    }

                    IFluidHandler handler = getFluidHandler(neighborBlockEntity, direction.getOpposite());
                    if (containsAllowedFuel(handler)) {
                        return handler;
                    }
                }
            }

            if (nodesAtCurrentDepth == 0) {
                nodesAtCurrentDepth = queue.size();
                currentDepth++;
            }
        }

        return null;
    }

    public static boolean transferFuel(SmartBlockEntity blockEntity, Object fuelTank, IFluidHandler sourceHandler,
                                       int transferRate) {
        if (fuelTank == null || sourceHandler == null || transferRate <= 0) {
            return false;
        }

        int space = TFMGEngineProxy.getTankSpace(fuelTank);
        if (space <= 0) {
            return false;
        }

        FluidStack toTransfer = FluidStack.EMPTY;
        for (int i = 0; i < sourceHandler.getTanks(); i++) {
            FluidStack fluid = sourceHandler.getFluidInTank(i);
            if (!fluid.isEmpty() && TFMGEngineProxy.isAllowedFuel(fluid.getFluid())) {
                toTransfer = fluid.copy();
                toTransfer.setAmount(Math.min(Math.min(transferRate, space), fluid.getAmount()));
                break;
            }
        }

        if (toTransfer.isEmpty()) {
            return false;
        }

        FluidStack drained = sourceHandler.drain(toTransfer, FluidAction.SIMULATE);
        if (drained.isEmpty() || !TFMGEngineProxy.isAllowedFuel(drained.getFluid())) {
            return false;
        }

        int filled = TFMGEngineProxy.fillFuelTank(fuelTank, drained);
        if (filled <= 0) {
            return false;
        }

        FluidStack toDrain = drained.copy();
        toDrain.setAmount(filled);
        sourceHandler.drain(toDrain, FluidAction.EXECUTE);
        blockEntity.setChanged();
        return true;
    }

    private static List<Direction> getConnectedDirections(SmartBlockEntity blockEntity) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return List.of(Direction.values());
        }

        FluidTransportBehaviour pipe = FluidPropagator.getPipe(level, blockEntity.getBlockPos());
        if (pipe == null) {
            List<Direction> fallback = new ArrayList<>(Direction.values().length);
            for (Direction direction : Direction.values()) {
                fallback.add(direction);
            }
            return fallback;
        }

        return FluidPropagator.getPipeConnections(blockEntity.getBlockState(), pipe);
    }

    private static boolean containsAllowedFuel(@Nullable IFluidHandler handler) {
        if (handler == null) {
            return false;
        }

        for (int i = 0; i < handler.getTanks(); i++) {
            FluidStack fluid = handler.getFluidInTank(i);
            if (!fluid.isEmpty() && TFMGEngineProxy.isAllowedFuel(fluid.getFluid())) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    private static IFluidHandler getFluidHandler(@Nullable BlockEntity blockEntity, Direction side) {
        if (blockEntity == null) {
            return null;
        }

        LazyOptional<IFluidHandler> capability = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, side);
        return capability.resolve().orElse(null);
    }

    public record FuelTarget(Object fuelTank, BlockPos enginePos) {
    }
}
