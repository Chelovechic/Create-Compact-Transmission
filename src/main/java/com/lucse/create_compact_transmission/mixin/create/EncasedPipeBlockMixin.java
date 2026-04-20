package com.lucse.create_compact_transmission.mixin.create;

import com.lucse.create_compact_transmission.content.fuelinjector.FuelInjectorSupport;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.simibubi.create.content.fluids.pipes.EncasedPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EncasedPipeBlock.class, remap = false)
public class EncasedPipeBlockMixin {

    @Inject(method = "handleEncasing", at = @At("HEAD"), remap = false)
    private void cct$captureBeforeEncasing(BlockState state, Level level, BlockPos pos, ItemStack heldItem,
                                           Player player, InteractionHand hand, BlockHitResult ray, CallbackInfo ci,
                                           @Share("hadFuelInjector") LocalBooleanRef hadFuelInjector) {
        hadFuelInjector.set(FuelInjectorSupport.hasInstalledInjector(level.getBlockEntity(pos)));
    }

    @Inject(method = "handleEncasing", at = @At("TAIL"), remap = false)
    private void cct$restoreAfterEncasing(BlockState state, Level level, BlockPos pos, ItemStack heldItem,
                                          Player player, InteractionHand hand, BlockHitResult ray, CallbackInfo ci,
                                          @Share("hadFuelInjector") LocalBooleanRef hadFuelInjector) {
        if (!hadFuelInjector.get()) {
            return;
        }
        FuelInjectorSupport.restoreInstalledInjector(level, pos);
    }

    @Inject(method = "onWrenched", at = @At("HEAD"), remap = false)
    private void cct$captureBeforeWrench(BlockState state, UseOnContext context,
                                         CallbackInfoReturnable<InteractionResult> cir,
                                         @Share("hadFuelInjector") LocalBooleanRef hadFuelInjector) {
        hadFuelInjector.set(FuelInjectorSupport.hasInstalledInjector(
                context.getLevel().getBlockEntity(context.getClickedPos())));
    }

    @Inject(method = "onWrenched", at = @At("RETURN"), remap = false)
    private void cct$restoreAfterWrench(BlockState state, UseOnContext context,
                                        CallbackInfoReturnable<InteractionResult> cir,
                                        @Share("hadFuelInjector") LocalBooleanRef hadFuelInjector) {
        if (!hadFuelInjector.get() || context.getLevel().isClientSide
                || cir.getReturnValue() != InteractionResult.SUCCESS) {
            return;
        }
        FuelInjectorSupport.restoreInstalledInjector(context.getLevel(), context.getClickedPos());
    }
}
