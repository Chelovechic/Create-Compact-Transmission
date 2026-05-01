package com.lucse.create_compact_transmission;

import com.lucse.create_compact_transmission.content.fuelinjector.FuelInjectorBehaviour;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class CCTCommonEvents {

    @SubscribeEvent
    public void onBlockEntityBehaviours(BlockEntityBehaviourEvent event) {
        event.forType(AllBlockEntityTypes.FLUID_PIPE.get(), CCTCommonEvents::attachFuelInjectorIfMissing);
        event.forType(AllBlockEntityTypes.ENCASED_FLUID_PIPE.get(), CCTCommonEvents::attachFuelInjectorIfMissing);
    }

    private static void attachFuelInjectorIfMissing(FluidPipeBlockEntity pipeBlockEntity) {
        if (FuelInjectorBehaviour.get(pipeBlockEntity) == null) {
            pipeBlockEntity.attachBehaviourLate(new FuelInjectorBehaviour(pipeBlockEntity));
        }
    }

    @SubscribeEvent
    public void onPipeBroken(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof Level level) || level.isClientSide || event.getPlayer() == null
                || event.getPlayer().isCreative()) {
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(event.getPos());
        if (!(blockEntity instanceof FluidPipeBlockEntity pipeBlockEntity)) {
            return;
        }

        FuelInjectorBehaviour behaviour = FuelInjectorBehaviour.get(pipeBlockEntity);
        if (behaviour == null || !behaviour.isInstalled()) {
            return;
        }

        Block.popResource(level, event.getPos(), CCTItems.FUEL_INJECTOR.asStack());
    }
}
