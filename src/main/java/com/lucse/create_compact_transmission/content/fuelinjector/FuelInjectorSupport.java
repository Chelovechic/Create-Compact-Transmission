package com.lucse.create_compact_transmission.content.fuelinjector;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class FuelInjectorSupport {

    private FuelInjectorSupport() {
    }

    public static boolean hasInstalledInjector(BlockEntity blockEntity) {
        if (!(blockEntity instanceof SmartBlockEntity smartBlockEntity)) {
            return false;
        }

        FuelInjectorBehaviour behaviour = FuelInjectorBehaviour.get(smartBlockEntity);
        return behaviour != null && behaviour.isInstalled();
    }

    public static void restoreInstalledInjector(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof SmartBlockEntity smartBlockEntity)) {
            return;
        }

        FuelInjectorBehaviour behaviour = FuelInjectorBehaviour.getOrCreate(smartBlockEntity);
        if (!behaviour.isInstalled()) {
            behaviour.install();
        }
    }
}
