package com.lucse.create_compact_transmission.content.compactspeedregulator;

import java.util.List;

import com.lucse.create_compact_transmission.content.kinetics.KineticSpeedLimiter;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.motor.KineticScrollValueBehaviour;
import com.simibubi.create.content.kinetics.transmission.ClutchBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;

import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CompactSpeedRegulatorBlockEntity extends ClutchBlockEntity {

    public static final int DEFAULT_SPEED = 16;
    public ScrollValueBehaviour targetSpeed;

    public CompactSpeedRegulatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        Integer max = AllConfigs.server().kinetics.maxRotationSpeed.get();

        targetSpeed = new KineticScrollValueBehaviour(CreateLang.translateDirect("kinetics.speed_controller.rotation_speed"),
            this, new RegulatorValueBoxTransform());
        targetSpeed.between(-max, max);
        targetSpeed.value = DEFAULT_SPEED;
        targetSpeed.withCallback(i -> this.updateTargetRotation());
        behaviours.add(targetSpeed);
    }

    private void updateTargetRotation() {
        if (hasNetwork())
            getOrCreateNetwork().remove(this);
        RotationPropagator.handleRemoved(level, worldPosition, this);
        removeSource();
        attachKinetics();
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (!hasSource()) {
            return 1;
        }

        Direction sourceFacing = getSourceFacing();
        if (face == sourceFacing) {
            return 1;
        }
        float targetSpeedValue = targetSpeed.getValue();
        float inputSpeed = KineticSpeedLimiter.getInputSpeed(this);
        if (targetSpeedValue == 0) {
            return 0;
        }
        if (inputSpeed == 0) {
            return 0;
        }
        return KineticSpeedLimiter.clampModifier(this, targetSpeedValue / inputSpeed);
    }

    private class RegulatorValueBoxTransform extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 11f, 15.5f);
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            if (direction.getAxis().isVertical())
                return false;
            Direction.Axis blockAxis = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS);
            return direction.getAxis() != blockAxis;
        }

        @Override
        public float getScale() {
            return 0.5f;
        }

    }

}
