package com.lucse.create_compact_transmission.content.smartspeeddoubler;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class SmartSpeedDoublerConfigurationPacket extends BlockEntityConfigurationPacket<SmartSpeedDoublerBlockEntity> {

    private CompoundTag data;

    public SmartSpeedDoublerConfigurationPacket(BlockPos pos, CompoundTag data) {
        super(pos);
        this.data = data;
    }

    public SmartSpeedDoublerConfigurationPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void applySettings(SmartSpeedDoublerBlockEntity be) {
        be.multiplierRatio = data.getInt("multiplier");
        be.multiplierRatioTop = data.getInt("multiplierTop");
        be.multiplierRatioBottom = data.getInt("multiplierBottom");
        be.setChanged();
        be.sendData();
        if (be.getLevel() != null) {
            RotationPropagator.handleRemoved(be.getLevel(), be.getBlockPos(), be);
            RotationPropagator.handleAdded(be.getLevel(), be.getBlockPos(), be);
        }
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        data = buffer.readNbt();
    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeNbt(data);
    }
}

