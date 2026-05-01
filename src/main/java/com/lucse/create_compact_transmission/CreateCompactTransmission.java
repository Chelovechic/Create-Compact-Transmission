package com.lucse.create_compact_transmission;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

@Mod(CreateCompactTransmission.MODID)
public class CreateCompactTransmission {
    public static final String MODID = "create_compact_transmission";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static IEventBus modEventBus;
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    public CreateCompactTransmission(IEventBus eventBus, ModContainer modContainer) {
        modEventBus = eventBus;
        IEventBus forgeEventBus = NeoForge.EVENT_BUS;
        REGISTRATE.registerEventListeners(modEventBus);

        CCTSoundEvents.register(modEventBus);
        forgeEventBus.register(new CCTCommonEvents());

        CCTSoundEvents.prepare();

        REGISTRATE.setCreativeTab(CCTCreativeTabs.MAIN);
        CCTBlocks.register();
        CCTItems.register();
        CCTBlockEntityTypes.register();
        CCTCreativeTabs.register(modEventBus);
        CCTPackets.registerPackets();

        if (FMLEnvironment.dist.isClient()) {
            CreateCompactTransmissionClient.onCtorClient(modEventBus, forgeEventBus);
        }
    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
