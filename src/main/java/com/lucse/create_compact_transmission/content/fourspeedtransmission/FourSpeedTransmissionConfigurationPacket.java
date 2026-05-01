package com.lucse.create_compact_transmission.content.fourspeedtransmission;

import com.lucse.create_compact_transmission.CCTPackets;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class FourSpeedTransmissionConfigurationPacket extends BlockEntityConfigurationPacket<FourSpeedTransmissionBlockEntity> {

    public static final StreamCodec<RegistryFriendlyByteBuf, FourSpeedTransmissionConfigurationPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            ByteBufCodecs.COMPOUND_TAG, packet -> packet.data,
            FourSpeedTransmissionConfigurationPacket::new
    );

    private final CompoundTag data;

    public FourSpeedTransmissionConfigurationPacket(BlockPos pos, CompoundTag data) {
        super(pos);
        this.data = data;
    }

    @Override
    protected void applySettings(ServerPlayer player, FourSpeedTransmissionBlockEntity be) {
        be.applyConfiguration(data.getInt("GearCount"), data.getIntArray("GearRatios"));
        if (be.getLevel() != null) {
            RotationPropagator.handleRemoved(be.getLevel(), be.getBlockPos(), be);
            RotationPropagator.handleAdded(be.getLevel(), be.getBlockPos(), be);
        }
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return CCTPackets.CONFIGURE_FOUR_SPEED_TRANSMISSION;
    }
}
