package com.lucse.create_compact_transmission.content.co2scrubber;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.SmartFluidPipeBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Queue;

public class CO2ScrubberBlockEntity extends SmartBlockEntity {

    private static final int DRAIN_RATE = 200;
    private static final int TICK_RATE = 5;

    private static final String TFMG_ENGINE_CLASS = "com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity";
    private static final String TFMG_CO2_FLUID_CLASS = "com.drmangotea.tfmg.registry.TFMGFluids";
    private static Class<?> engineClass;
    private static Object carbonDioxideFluid;
    private static Field exhaustTankField;

    static {
        try {
            engineClass = Class.forName(TFMG_ENGINE_CLASS);
            Class<?> fluidsClass = Class.forName(TFMG_CO2_FLUID_CLASS);
            Field carbonDioxideField = fluidsClass.getField("CARBON_DIOXIDE");
            Object carbonDioxideEntry = carbonDioxideField.get(null);
            carbonDioxideFluid = carbonDioxideEntry.getClass().getMethod("get").invoke(carbonDioxideEntry);
            exhaustTankField = engineClass.getField("exhaustTank");
        } catch (Exception e) {
            engineClass = null;
            carbonDioxideFluid = null;
            exhaustTankField = null;
        }
    }

    private int tickCounter = 0;
    public int smokeTimer = 0;

    public CO2ScrubberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        setLazyTickRate(TICK_RATE);
    }

    @Override
    public void tick() {
        super.tick();
        
        if (level == null)
            return;
        if (engineClass == null)
            return;
        if (!level.isClientSide) {
            tickCounter++;
            if (tickCounter >= TICK_RATE) {
                tickCounter = 0;
                processNeighbors();
            }
        }
        if (level.isClientSide) {
            boolean isActive = getBlockState().getValue(CO2ScrubberBlock.ACTIVE);
            if (isActive) {
                if (smokeTimer > 0) {
                    smokeTimer--;
                    if ((level.getGameTime() + getBlockPos().hashCode()) % 5 == 0) {
                        makeParticles(level, getBlockPos());
                    }
                } else {
                    smokeTimer = 100;
                }
            } else {
                smokeTimer = 0;
            }
        }
    }

    private void processNeighbors() {
        boolean wasActive = getBlockState().getValue(CO2ScrubberBlock.ACTIVE);
        boolean isActive = false;
        Direction connectionDirection = getBlockState().getValue(CO2ScrubberBlock.FACING).getOpposite();
        BlockPos connectionPos = worldPosition.relative(connectionDirection);
        BlockEntity directEngine = level.getBlockEntity(connectionPos);

        if (directEngine != null && engineClass.isInstance(directEngine)) {
            isActive = drainCO2FromEngine(directEngine);
        } else {
            BlockState connectionState = level.getBlockState(connectionPos);
            if (isCreatePipe(connectionState.getBlock())
                    && CO2ScrubberBlock.canConnectTo(level, connectionPos, connectionState, connectionDirection)) {
                BlockEntity engineThroughPipe = findEngineThroughPipe(connectionPos);
                if (engineThroughPipe != null) {
                    isActive = drainCO2FromEngine(engineThroughPipe);
                }
            }
        }
        if (isActive != wasActive) {
            level.setBlock(worldPosition, getBlockState().setValue(CO2ScrubberBlock.ACTIVE, isActive), 2);
            if (isActive) {
                smokeTimer = 100;
                sendData();
            }
        }
    }

    private boolean drainCO2FromEngine(BlockEntity engine) {
        try {
            Object exhaustTank = exhaustTankField.get(engine);
            boolean isEmpty = (Boolean) exhaustTank.getClass().getMethod("isEmpty").invoke(exhaustTank);
            if (!isEmpty) {
                FluidStack drainStack = new FluidStack(
                    (net.minecraft.world.level.material.Fluid) carbonDioxideFluid,
                    DRAIN_RATE
                );
                FluidStack drained = (FluidStack) exhaustTank.getClass()
                    .getMethod("drain", FluidStack.class, IFluidHandler.FluidAction.class)
                    .invoke(exhaustTank, drainStack, IFluidHandler.FluidAction.EXECUTE);
                
                if (!drained.isEmpty()) {
                    setChanged();
                    sendData();
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private boolean isCreatePipe(Block block) {
        return block instanceof FluidPipeBlock 
            || block instanceof SmartFluidPipeBlock
            || block == AllBlocks.ENCASED_FLUID_PIPE.get()
            || block == AllBlocks.GLASS_FLUID_PIPE.get();
    }

    private BlockEntity findEngineThroughPipe(BlockPos startPos) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        queue.add(startPos);
        visited.add(startPos);
        visited.add(worldPosition);
        int maxDepth = 20;
        int currentDepth = 0;
        int nodesAtCurrentDepth = 1;
        
        while (!queue.isEmpty() && currentDepth < maxDepth) {
            BlockPos currentPos = queue.poll();
            nodesAtCurrentDepth--;
            FluidTransportBehaviour pipe = FluidPropagator.getPipe(level, currentPos);
            if (pipe == null) {
                if (nodesAtCurrentDepth == 0) {
                    nodesAtCurrentDepth = queue.size();
                    currentDepth++;
                }
                continue;
            }
            
            BlockState currentState = level.getBlockState(currentPos);
            java.util.List<Direction> connections = FluidPropagator.getPipeConnections(currentState, pipe);
            
            for (Direction direction : connections) {
                BlockPos neighborPos = currentPos.relative(direction);
                
                if (visited.contains(neighborPos)) {
                    continue;
                }
                
                visited.add(neighborPos);
                BlockEntity be = level.getBlockEntity(neighborPos);
                if (be != null && engineClass.isInstance(be)) {
                    return be;
                }

                BlockState neighborState = level.getBlockState(neighborPos);
                FluidTransportBehaviour neighborPipe = FluidPropagator.getPipe(level, neighborPos);
                if (neighborPipe != null && neighborPipe.canHaveFlowToward(neighborState, direction.getOpposite())) {
                    queue.add(neighborPos);
                }
            }
            if (nodesAtCurrentDepth == 0) {
                nodesAtCurrentDepth = queue.size();
                currentDepth++;
            }
        }
        return null;
    }

    public void updateNeighbors() {
        if (level == null || level.isClientSide)
            return;
        processNeighbors();
    }

    public static void makeParticles(Level level, BlockPos pos) {
        Random random = Create.RANDOM;
        level.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, 
            pos.getX() + 0.5 + (random.nextFloat() - 0.5) * 0.3, 
            pos.getY() + 1.0, 
            pos.getZ() + 0.5 + (random.nextFloat() - 0.5) * 0.3, 
            0.0D, 0.08D, 0.0D);
    }

    @Override
    protected void read(net.minecraft.nbt.CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if (clientPacket) {
            smokeTimer = compound.getInt("SmokeTimer");
        }
    }

    @Override
    protected void write(net.minecraft.nbt.CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        if (clientPacket) {
            boolean isActive = getBlockState().getValue(CO2ScrubberBlock.ACTIVE);
            compound.putInt("SmokeTimer", isActive ? 100 : 0);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
}
