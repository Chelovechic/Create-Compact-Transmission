package com.lucse.create_compact_transmission;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue C02SCRUB_POWER = BUILDER
            .comment("c2scrubber power")
            .defineInRange("c02scrub_power", 200, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue FOUR_KPP_MAX = BUILDER
            .comment("Max absolute gear ratio for the four-speed transmission,(400 = 4.00x).")
            .defineInRange("four_kpp_max", 400, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SIMPLE_KPP_MAX = BUILDER
            .comment("Max absolute speed multiplier for the simple gear box ")
            .defineInRange("simple_kpp_max", 400, 1, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
