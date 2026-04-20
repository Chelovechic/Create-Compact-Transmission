package com.lucse.create_compact_transmission;

import com.lucse.create_compact_transmission.content.fuelinjector.FuelInjectorBehaviour;
import com.simibubi.create.api.event.BlockEntityBehaviourEvent;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CCTCommonEvents {

    @SubscribeEvent
    public void onBlockEntityBehaviours(BlockEntityBehaviourEvent<?> event) {
        if (event.getBlockEntity() instanceof FluidPipeBlockEntity pipeBlockEntity
                && (event.getBlockState().getBlock() instanceof FluidPipeBlock
                || event.getBlockState().getBlock() instanceof EncasedPipeBlock)) {
            if (FuelInjectorBehaviour.get(pipeBlockEntity) != null) {
                return;
            }
            event.attach(new FuelInjectorBehaviour(pipeBlockEntity));
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
