package com.lucse.create_compact_transmission;

import com.lucse.create_compact_transmission.content.fuelinjector.FuelInjectorItem;
import com.lucse.create_compact_transmission.content.gearboxsetting.GearboxSettingItem;
import com.lucse.create_compact_transmission.content.moderninjector.ModernInjectorItem;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;

public class CCTItems {
    private static final CreateRegistrate REGISTRATE = CreateCompactTransmission.getRegistrate();

    static {
        REGISTRATE.setCreativeTab(CCTCreativeTabs.MAIN);
    }

    public static final ItemEntry<ModernInjectorItem> MODERN_INJECTOR =
            REGISTRATE.item("modern_injector", ModernInjectorItem::new)
                    .lang("Modern Injector")
                    .register();

    public static final ItemEntry<FuelInjectorItem> FUEL_INJECTOR =
            REGISTRATE.item("fuel_injector", FuelInjectorItem::new)
                    .lang("Fuel Injector")
                    .register();

    public static final ItemEntry<GearboxSettingItem> GEARBOX_SETTING =
            REGISTRATE.item("gearbox_setting", GearboxSettingItem::new)
                    .properties(p -> p.stacksTo(1))
                    .lang("Gearbox Setting")
                    .register();

    public static void register() {
    }
}
