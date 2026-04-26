package com.lucse.create_compact_transmission.content.fourspeedtransmission;

import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class FourSpeedTransmissionConfigurationPacket extends BlockEntityConfigurationPacket<FourSpeedTransmissionBlockEntity> {

    private CompoundTag data;

    public FourSpeedTransmissionConfigurationPacket(BlockPos pos, CompoundTag data) {
        super(pos);
        this.data = data;
    }

    public FourSpeedTransmissionConfigurationPacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    @Override
    protected void applySettings(FourSpeedTransmissionBlockEntity be) {
        be.applyConfiguration(data.getInt("GearCount"), data.getIntArray("GearRatios"));
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
