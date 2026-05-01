package com.lucse.create_compact_transmission.content.smartspeeddoubler;

import com.lucse.create_compact_transmission.CCTPackets;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class SmartSpeedDoublerConfigurationPacket extends BlockEntityConfigurationPacket<SmartSpeedDoublerBlockEntity> {

    public static final StreamCodec<RegistryFriendlyByteBuf, SmartSpeedDoublerConfigurationPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.pos,
            ByteBufCodecs.COMPOUND_TAG, packet -> packet.data,
            SmartSpeedDoublerConfigurationPacket::new
    );

    private final CompoundTag data;

    public SmartSpeedDoublerConfigurationPacket(BlockPos pos, CompoundTag data) {
        super(pos);
        this.data = data;
    }

    @Override
    protected void applySettings(ServerPlayer player, SmartSpeedDoublerBlockEntity be) {
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
    public PacketTypeProvider getTypeProvider() {
        return CCTPackets.CONFIGURE_SMART_SPEED_DOUBLER;
    }
}
