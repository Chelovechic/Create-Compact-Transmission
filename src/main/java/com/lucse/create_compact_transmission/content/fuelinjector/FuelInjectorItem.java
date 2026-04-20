package com.lucse.create_compact_transmission.content.fuelinjector;

import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.fluids.pipes.FluidPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class FuelInjectorItem extends Item {

    public FuelInjectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof FluidPipeBlock) && !(state.getBlock() instanceof EncasedPipeBlock)) {
            return InteractionResult.PASS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof FluidPipeBlockEntity fluidPipeBlockEntity)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide || player == null) {
            return InteractionResult.SUCCESS;
        }

        FuelInjectorBehaviour behaviour = FuelInjectorBehaviour.getOrCreate(fluidPipeBlockEntity);
        if (behaviour.isInstalled()) {
            player.displayClientMessage(
                    Component.translatable("item.create_compact_transmission.fuel_injector.already_installed"), true);
            return InteractionResult.FAIL;
        }

        behaviour.install();
        level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 1.2f);

        if (!player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }

        player.displayClientMessage(
                Component.translatable("item.create_compact_transmission.fuel_injector.installed"), true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.create_compact_transmission.fuel_injector.tooltip"));
    }
}
