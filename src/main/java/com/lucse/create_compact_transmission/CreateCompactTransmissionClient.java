package com.lucse.create_compact_transmission;

import com.lucse.create_compact_transmission.content.fourspeedtransmission.FourSpeedTransmissionScreen;
import com.lucse.create_compact_transmission.content.smartspeeddoubler.SmartSpeedDoublerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CreateCompactTransmission.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreateCompactTransmissionClient {
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(CreateCompactTransmissionClient::init);
    }

    public static void init(final FMLClientSetupEvent event) {
        
    }
}

