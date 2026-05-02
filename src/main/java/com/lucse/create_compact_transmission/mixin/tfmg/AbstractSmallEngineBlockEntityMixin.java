package com.lucse.create_compact_transmission.mixin.tfmg;

import com.lucse.create_compact_transmission.content.moderninjector.ModernInjectorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.drmangotea.tfmg.content.engines.types.AbstractSmallEngineBlockEntity", remap = false)
public abstract class AbstractSmallEngineBlockEntityMixin extends BlockEntity {

    public AbstractSmallEngineBlockEntityMixin() {
        super(null, null, null);
    }

    @ModifyArg(
            method = "addToGoggleTooltip(Ljava/util/List;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/drmangotea/tfmg/base/lang/TFMGTexts$Engine;fuelConsumption(F)Lnet/createmod/catnip/lang/LangBuilder;",
                    remap = false
            ),
            index = 0,
            remap = false
    )
    private float modifyFuelConsumptionTooltip(float originalConsumption) {
        if (!ModernInjectorItem.isInstalled(this)) {
            return originalConsumption;
        }
        return originalConsumption * ModernInjectorItem.FUEL_CONSUMPTION_MULTIPLIER;
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

