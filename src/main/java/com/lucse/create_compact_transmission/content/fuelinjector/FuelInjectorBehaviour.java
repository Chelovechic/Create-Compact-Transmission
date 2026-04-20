package com.lucse.create_compact_transmission.content.fuelinjector;

import com.lucse.create_compact_transmission.content.fueling.TFMGEngineProxy;
import com.lucse.create_compact_transmission.content.fueling.TFMGPipeFuelingHelper;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class FuelInjectorBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<FuelInjectorBehaviour> TYPE = new BehaviourType<>();

    private static final int TICK_RATE = 5;
    private static final int TRANSFER_RATE = 100;

    private boolean installed;
    private int tickCounter;

    public FuelInjectorBehaviour(SmartBlockEntity blockEntity) {
        super(blockEntity);
    }

    public static FuelInjectorBehaviour get(SmartBlockEntity blockEntity) {
        return blockEntity.getBehaviour(TYPE);
    }

    public static FuelInjectorBehaviour getOrCreate(SmartBlockEntity blockEntity) {
        FuelInjectorBehaviour behaviour = get(blockEntity);
        if (behaviour != null) {
            return behaviour;
        }

        FuelInjectorBehaviour created = new FuelInjectorBehaviour(blockEntity);
        blockEntity.attachBehaviourLate(created);
        return created;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void install() {
        installed = true;
        tickCounter = 0;
        blockEntity.setChanged();
        blockEntity.sendData();
    }

    @Override
    public void tick() {
        super.tick();

        if (!installed || blockEntity.getLevel() == null || blockEntity.getLevel().isClientSide
                || !TFMGEngineProxy.isAvailable()) {
            return;
        }

        tickCounter++;
        if (tickCounter < TICK_RATE) {
            return;
        }
        tickCounter = 0;

        TFMGPipeFuelingHelper.FuelTarget fuelTarget = TFMGPipeFuelingHelper.findAdjacentEngine(blockEntity);
        if (fuelTarget == null) {
            return;
        }

        IFluidHandler sourceHandler = TFMGPipeFuelingHelper.findFuelSource(blockEntity, fuelTarget.enginePos());
        if (sourceHandler != null) {
            TFMGPipeFuelingHelper.transferFuel(blockEntity, fuelTarget.fuelTank(), sourceHandler, TRANSFER_RATE);
        }
    }

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        installed = nbt.getBoolean("FuelInjectorInstalled");
        if (!installed) {
            tickCounter = 0;
        }
    }

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        if (installed) {
            nbt.putBoolean("FuelInjectorInstalled", true);
        }
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
