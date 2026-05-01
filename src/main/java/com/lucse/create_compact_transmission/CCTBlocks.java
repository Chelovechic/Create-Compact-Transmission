package com.lucse.create_compact_transmission;

import com.lucse.create_compact_transmission.content.co2scrubber.CO2ScrubberBlock;
import com.lucse.create_compact_transmission.content.compactspeedregulator.CompactSpeedRegulatorBlock;
import com.lucse.create_compact_transmission.content.fourspeedtransmission.FourSpeedTransmissionBlock;
import com.lucse.create_compact_transmission.content.fourspeedtransmission.TransmissionGear;
import com.lucse.create_compact_transmission.content.rotator.RotatorBlock;
import com.lucse.create_compact_transmission.content.smartspeeddoubler.SmartSpeedDoublerBlock;
import com.lucse.create_compact_transmission.content.speedcompactchanger.SpeedCompactChangerBlock;
import com.lucse.create_compact_transmission.content.speeddoubler.SpeedDoublerBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;

import static com.simibubi.create.foundation.data.AssetLookup.forPowered;
import static com.simibubi.create.foundation.data.BlockStateGen.axisBlock;
import static com.simibubi.create.foundation.data.BlockStateGen.axisBlockProvider;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;

public class CCTBlocks {
    private static final CreateRegistrate REGISTRATE = CreateCompactTransmission.getRegistrate();

    static {
        REGISTRATE.setCreativeTab(CCTCreativeTabs.MAIN);
    }

    public static final BlockEntry<CompactSpeedRegulatorBlock> COMPACT_SPEED_REGULATOR = REGISTRATE.block("compact_speed_regulator", CompactSpeedRegulatorBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .blockstate(axisBlockProvider(false))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SpeedCompactChangerBlock> SPEED_COMPACT_CHANGER = REGISTRATE.block("speed_compact_changer", SpeedCompactChangerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> axisBlock(c, p, forPowered(c, p)))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<RotatorBlock> ROTATOR = REGISTRATE.block("rotator", RotatorBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> {})
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SpeedDoublerBlock> SPEED_DOUBLER = REGISTRATE.block("speed_doubler", SpeedDoublerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> axisBlock(c, p, forPowered(c, p)))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<FourSpeedTransmissionBlock> FOUR_SPEED_TRANSMISSION = REGISTRATE.block("four_speed_transmission", FourSpeedTransmissionBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .blockstate(fourSpeedTransmission())
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SmartSpeedDoublerBlock> SMART_SPEED_DOUBLER = REGISTRATE.block("smart_speed_doubler", SmartSpeedDoublerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.PODZOL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> axisBlock(c, p, forPowered(c, p)))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<CO2ScrubberBlock> CO2_SCRUBBER = REGISTRATE.block("co2_scrubber", CO2ScrubberBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.noOcclusion().mapColor(MapColor.METAL))
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .blockstate((c, p) -> {
                ModelFile model = p.models().getExistingFile(p.modLoc("block/" + c.getName()));
                p.getVariantBuilder(c.getEntry())
                        .forAllStatesExcept(state -> {
                            Direction dir = state.getValue(BlockStateProperties.FACING);
                            int xRot = 0;
                            int yRot = 0;
                            
                            switch (dir) {
                                case DOWN -> xRot = 180;
                                case UP -> {
                                    xRot = 0;
                                    yRot = 0;
                                }
                                case NORTH -> {
                                    xRot = 90;
                                    yRot = 0;
                                }
                                case SOUTH -> {
                                    xRot = 90;
                                    yRot = 180;
                                }
                                case EAST -> {
                                    xRot = 90;
                                    yRot = 90;
                                }
                                case WEST -> {
                                    xRot = 90;
                                    yRot = 270;
                                }
                            }
                            
                            return ConfiguredModel.builder()
                                    .modelFile(model)
                                    .rotationX(xRot)
                                    .rotationY(yRot)
                                    .build();
                        }, CO2ScrubberBlock.ACTIVE);
            })
            .item()
            .transform(customItemModel())
            .register();

    private static NonNullBiConsumer<DataGenContext<Block, FourSpeedTransmissionBlock>, RegistrateBlockstateProvider> fourSpeedTransmission() {
        return (c, p) -> {
            p.getVariantBuilder(c.getEntry())
                    .forAllStatesExcept(state -> {
                        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
                        TransmissionGear gear = state.getValue(FourSpeedTransmissionBlock.GEAR);
                        
                        String modelSuffix = switch (gear) {
                            case NEUTRAL -> "";
                            case REVERSE -> "_reverse";
                            case FIRST -> "_first";
                            case SECOND -> "_second";
                            case THIRD -> "_third";
                            case FOURTH -> "_fourth";
                        };
                        
                        ModelFile model = p.models().getExistingFile(p.modLoc("block/four_speed_transmission/block" + modelSuffix));
                        
                        int xRot = 0;
                        int yRot = 0;
                        
                        if (axis == Direction.Axis.X) {
                            xRot = 90;
                            yRot = 90;
                        } else if (axis == Direction.Axis.Z) {
                            xRot = 90;
                            yRot = 180;
                        }
                        
                        return ConfiguredModel.builder()
                                .modelFile(model)
                                .rotationX(xRot)
                                .rotationY(yRot)
                                .build();
                    }, BlockStateProperties.WATERLOGGED);
        };
    }

    public static void register() {
    }
}
