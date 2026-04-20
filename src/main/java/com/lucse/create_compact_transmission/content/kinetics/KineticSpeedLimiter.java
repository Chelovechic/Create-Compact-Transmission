package com.lucse.create_compact_transmission.content.kinetics;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class KineticSpeedLimiter {

    private KineticSpeedLimiter() {
    }

    public static float getInputSpeed(KineticBlockEntity blockEntity) {
        if (blockEntity == null) {
            return 0;
        }
        if (blockEntity.getLevel() == null) {
            return Math.abs(blockEntity.getTheoreticalSpeed());
        }

        BlockPos sourcePos = blockEntity.source;
        if (sourcePos != null) {
            BlockEntity sourceBlockEntity = blockEntity.getLevel().getBlockEntity(sourcePos);
            if (sourceBlockEntity instanceof KineticBlockEntity sourceKinetic) {
                return Math.abs(sourceKinetic.getTheoreticalSpeed());
            }
        }

        return Math.abs(blockEntity.getTheoreticalSpeed());
    }

    public static float clampModifier(KineticBlockEntity blockEntity, float requestedModifier) {
        if (!Float.isFinite(requestedModifier) || requestedModifier == 0) {
            return 0;
        }

        float inputSpeed = getInputSpeed(blockEntity);
        if (!Float.isFinite(inputSpeed) || inputSpeed <= 0) {
            return requestedModifier;
        }

        float maxSpeed = AllConfigs.server().kinetics.maxRotationSpeed.get().floatValue();
        if (!Float.isFinite(maxSpeed) || maxSpeed <= 0) {
            return 0;
        }

        float safeMaxSpeed = maxSpeed > 1 ? maxSpeed - 1.0f : maxSpeed;
        float absOutputSpeed = inputSpeed * Math.abs(requestedModifier);
        if (Float.isFinite(absOutputSpeed) && absOutputSpeed <= safeMaxSpeed) {
            return requestedModifier;
        }

        return Math.copySign(safeMaxSpeed / inputSpeed, requestedModifier);
    }
}
