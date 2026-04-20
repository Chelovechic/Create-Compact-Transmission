package com.lucse.create_compact_transmission;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(CreateCompactTransmission.MODID)
public class CreateCompactTransmission {
    public static final String MODID = "create_compact_transmission";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static IEventBus modEventBus;
    private static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                        .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    public CreateCompactTransmission() {
        modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        REGISTRATE.registerEventListeners(modEventBus);

        modEventBus.addListener(CCTSoundEvents::register);
        MinecraftForge.EVENT_BUS.register(this);
        forgeEventBus.register(new CCTCommonEvents());

        CCTSoundEvents.prepare();

        REGISTRATE.setCreativeTab(CCTCreativeTabs.MAIN);
        CCTBlocks.register();
        CCTItems.register();
        CCTBlockEntityTypes.register();
        CCTCreativeTabs.register(modEventBus);
        CCTPackets.registerPackets();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreateCompactTransmissionClient.onCtorClient(modEventBus, forgeEventBus));
    }

    public static CreateRegistrate getRegistrate() {
        return REGISTRATE;
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }
}
