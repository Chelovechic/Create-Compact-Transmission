package com.lucse.create_compact_transmission.mixin.tfmg;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.HolderLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity", remap = false)
public abstract class AbstractEngineBlockEntityMixin extends BlockEntity {

    @Unique
    private boolean modernInjectorInstalled = false;

    public AbstractEngineBlockEntityMixin() {
        super(null, null, null);
    }

    @Inject(
            at = @At("TAIL"),
            method = "write(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;Z)V",
            remap = false
    )
    private void writeModernInjectorFlag(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        compound.putBoolean("modernInjectorInstalled", modernInjectorInstalled);
    }

    @Inject(
            at = @At("TAIL"),
            method = "read(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;Z)V",
            remap = false
    )
    private void readModernInjectorFlag(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        modernInjectorInstalled = compound.getBoolean("modernInjectorInstalled");
    }

    @ModifyArg(
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/drmangotea/tfmg/content/engines/base/EngineFluidTank;forceDrain(ILnet/neoforged/neoforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/neoforged/neoforge/fluids/FluidStack;",
                    remap = false
            ),
            method = "manageFuelAndExhaust()V",
            remap = false,
            index = 0
    )
    private int modifyFuelDrainAmount(int originalAmount) {
        if (!modernInjectorInstalled) {
            return originalAmount;
        }
        if (originalAmount <= 0) {
            return 0;
        }
        return Math.max(1, (int) (originalAmount * 0.5f));
    }

    @Unique
    public void setModernInjectorInstalled(boolean installed) {
        this.modernInjectorInstalled = installed;
    }

    @Unique
    public boolean isModernInjectorInstalled() {
        return modernInjectorInstalled;
    }
}
