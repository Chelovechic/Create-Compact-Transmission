package com.lucse.create_compact_transmission;

import com.lucse.create_compact_transmission.content.fourspeedtransmission.FourSpeedTransmissionScreen;
import com.lucse.create_compact_transmission.content.smartspeeddoubler.SmartSpeedDoublerScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateCompactTransmissionClient {
    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(CreateCompactTransmissionClient::init);
    }

    public static void init(final FMLClientSetupEvent event) {
        
    }
}

