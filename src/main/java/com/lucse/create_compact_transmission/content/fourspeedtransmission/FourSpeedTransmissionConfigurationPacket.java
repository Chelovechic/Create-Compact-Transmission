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
        be.gearReverseRatio = data.getInt("gearR");
        be.gear1Ratio = data.getInt("gear1");
        be.gear2Ratio = data.getInt("gear2");
        be.gear3Ratio = data.getInt("gear3");
        be.gear4Ratio = data.getInt("gear4");
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

