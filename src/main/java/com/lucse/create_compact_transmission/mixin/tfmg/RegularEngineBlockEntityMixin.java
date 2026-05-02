package com.lucse.create_compact_transmission.mixin.tfmg;

import com.lucse.create_compact_transmission.content.moderninjector.ModernInjectorItem;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "com.drmangotea.tfmg.content.engines.types.regular_engine.RegularEngineBlockEntity", remap = false)
public abstract class RegularEngineBlockEntityMixin extends BlockEntity {

    public RegularEngineBlockEntityMixin() {
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
        return originalConsumption * 0.5F;
    }
}
