package com.lucse.create_compact_transmission.mixin.tfmg;

import com.lucse.create_compact_transmission.content.moderninjector.ModernInjectorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.drmangotea.tfmg.content.engines.types.AbstractSmallEngineBlockEntity", remap = false)
public abstract class AbstractSmallEngineBlockEntityMixin extends BlockEntity {

    public AbstractSmallEngineBlockEntityMixin() {
        super(null, null, null);
    }

    @Inject(
            at = @At("RETURN"),
            method = "getFuelConsumption()I",
            cancellable = true,
            remap = false
    )
    private void modifyFuelConsumption(CallbackInfoReturnable<Integer> cir) {
        if (!ModernInjectorItem.isInstalled(this)) {
            return;
        }
        int originalConsumption = cir.getReturnValue();
        if (originalConsumption <= 0) {
            return;
        }
        int modifiedConsumption = Math.max(1, (int) (originalConsumption * 0.5f));
        cir.setReturnValue(modifiedConsumption);
    }

    @Inject(
            at = @At("TAIL"),
            method = "remove()V",
            remap = false
    )
    private void dropModernInjector(CallbackInfo ci) {
        if (ModernInjectorItem.isInstalled(this) && this.hasLevel() && !this.getLevel().isClientSide) {
            try {
                java.lang.reflect.Method dropItemMethod = this.getClass().getSuperclass().getMethod("dropItem", ItemStack.class);
                ItemStack injectorStack = com.lucse.create_compact_transmission.CCTItems.MODERN_INJECTOR.asStack();
                dropItemMethod.invoke(this, injectorStack);
            } catch (Exception e) {
                net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                        this.getLevel(),
                        this.getBlockPos().getX() + 0.5,
                        this.getBlockPos().getY() + 0.3,
                        this.getBlockPos().getZ() + 0.5,
                        com.lucse.create_compact_transmission.CCTItems.MODERN_INJECTOR.asStack()
                );
                itemEntity.setDefaultPickUpDelay();
                itemEntity.setDeltaMovement(0, 0.15f, 0);
                this.getLevel().addFreshEntity(itemEntity);
            }
        }
    }
}

