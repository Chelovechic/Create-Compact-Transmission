package com.lucse.create_compact_transmission;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class CCTCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateCompactTransmission.MODID);

    public static final List<ItemProviderEntry<?>> ITEMS = List.of(
            CCTBlocks.COMPACT_SPEED_REGULATOR,
            CCTBlocks.SPEED_COMPACT_CHANGER,
            CCTBlocks.SPEED_DOUBLER,
            CCTBlocks.SMART_SPEED_DOUBLER,
            CCTBlocks.FOUR_SPEED_TRANSMISSION,
            CCTBlocks.ROTATOR,
            CCTBlocks.CO2_SCRUBBER,
            CCTItems.MODERN_INJECTOR,
            CCTItems.FUEL_INJECTOR,
            CCTItems.GEARBOX_SETTING
    );

    public static final RegistryObject<CreativeModeTab> MAIN = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.create_compact_transmission.main"))
            .icon(() -> CCTBlocks.COMPACT_SPEED_REGULATOR.asStack())
            .displayItems((params, output) -> {
                for (ItemProviderEntry<?> item : ITEMS) {
                    output.accept(item);
                }
            })
            .build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
