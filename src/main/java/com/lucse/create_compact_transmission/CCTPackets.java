package com.lucse.create_compact_transmission;

import com.lucse.create_compact_transmission.content.fourspeedtransmission.FourSpeedTransmissionConfigurationPacket;
import com.lucse.create_compact_transmission.content.smartspeeddoubler.SmartSpeedDoublerConfigurationPacket;

import net.createmod.catnip.net.base.BasePacketPayload;
import net.createmod.catnip.net.base.CatnipPacketRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Locale;

public enum CCTPackets implements BasePacketPayload.PacketTypeProvider {
    CONFIGURE_FOUR_SPEED_TRANSMISSION(
            FourSpeedTransmissionConfigurationPacket.class,
            FourSpeedTransmissionConfigurationPacket.STREAM_CODEC),
    CONFIGURE_SMART_SPEED_DOUBLER(
            SmartSpeedDoublerConfigurationPacket.class,
            SmartSpeedDoublerConfigurationPacket.STREAM_CODEC);

    private final CatnipPacketRegistry.PacketType<?> type;

    <T extends BasePacketPayload> CCTPackets(Class<T> clazz,
                                             StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        String name = name().toLowerCase(Locale.ROOT);
        this.type = new CatnipPacketRegistry.PacketType<>(
                new CustomPacketPayload.Type<>(CreateCompactTransmission.asResource(name)),
                clazz, codec
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType() {
        return (CustomPacketPayload.Type<T>) this.type.type();
    }

    public static void registerPackets() {
        CatnipPacketRegistry packetRegistry = new CatnipPacketRegistry(
                CreateCompactTransmission.MODID, String.valueOf(1)
        );
        for (CCTPackets packet : values()) {
            packetRegistry.registerPacket(packet.type);
        }
        packetRegistry.registerAllPackets();
    }
}
