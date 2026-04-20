package com.lucse.create_compact_transmission;

import com.lucse.create_compact_transmission.content.co2scrubber.CO2ScrubberBlockEntity;
import com.lucse.create_compact_transmission.content.compactspeedregulator.CompactSpeedRegulatorBlockEntity;
import com.lucse.create_compact_transmission.content.fourspeedtransmission.FourSpeedTransmissionBlockEntity;
import com.lucse.create_compact_transmission.content.rotator.RotatorBlockEntity;
import com.lucse.create_compact_transmission.content.smartspeeddoubler.SmartSpeedDoublerBlockEntity;
import com.lucse.create_compact_transmission.content.speedcompactchanger.SpeedCompactChangerBlockEntity;
import com.lucse.create_compact_transmission.content.speeddoubler.SpeedDoublerBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.encased.EncasedCogVisual;
import com.simibubi.create.content.kinetics.transmission.SplitShaftRenderer;
import com.simibubi.create.content.kinetics.transmission.SplitShaftVisual;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CCTBlockEntityTypes {
    private static final CreateRegistrate REGISTRATE = CreateCompactTransmission.getRegistrate();

    public static final BlockEntityEntry<CompactSpeedRegulatorBlockEntity> COMPACT_SPEED_REGULATOR = REGISTRATE
            .blockEntity("compact_speed_regulator", CompactSpeedRegulatorBlockEntity::new)
            .visual(() -> SplitShaftVisual::new, false)
            .validBlocks(CCTBlocks.COMPACT_SPEED_REGULATOR)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<SpeedCompactChangerBlockEntity> SPEED_COMPACT_CHANGER = REGISTRATE
            .blockEntity("speed_compact_changer", SpeedCompactChangerBlockEntity::new)
            .visual(() -> SplitShaftVisual::new, false)
            .validBlocks(CCTBlocks.SPEED_COMPACT_CHANGER)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<RotatorBlockEntity> ROTATOR = REGISTRATE
            .blockEntity("rotator", RotatorBlockEntity::new)
            .visual(() -> SplitShaftVisual::new, false)
            .validBlocks(CCTBlocks.ROTATOR)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<SpeedDoublerBlockEntity> SPEED_DOUBLER = REGISTRATE
            .blockEntity("speed_doubler", SpeedDoublerBlockEntity::new)
            .visual(() -> SplitShaftVisual::new, false)
            .validBlocks(CCTBlocks.SPEED_DOUBLER)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<FourSpeedTransmissionBlockEntity> FOUR_SPEED_TRANSMISSION = REGISTRATE
            .blockEntity("four_speed_transmission", FourSpeedTransmissionBlockEntity::new)
            .visual(() -> SplitShaftVisual::new, false)
            .validBlocks(CCTBlocks.FOUR_SPEED_TRANSMISSION)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<SmartSpeedDoublerBlockEntity> SMART_SPEED_DOUBLER = REGISTRATE
            .blockEntity("smart_speed_doubler", SmartSpeedDoublerBlockEntity::new)
            .visual(() -> SplitShaftVisual::new, false)
            .validBlocks(CCTBlocks.SMART_SPEED_DOUBLER)
            .renderer(() -> SplitShaftRenderer::new)
            .register();

    public static final BlockEntityEntry<CO2ScrubberBlockEntity> CO2_SCRUBBER = REGISTRATE
            .blockEntity("co2_scrubber", CO2ScrubberBlockEntity::new)
            .validBlocks(CCTBlocks.CO2_SCRUBBER)
            .register();

    public static void register() {
    }
}

