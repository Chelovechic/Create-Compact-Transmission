package com.lucse.create_compact_transmission.content.smartspeeddoubler;

import com.lucse.create_compact_transmission.Config;
import com.lucse.create_compact_transmission.content.kinetics.KineticSpeedLimiter;
import com.simibubi.create.content.kinetics.transmission.ClutchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class SmartSpeedDoublerBlockEntity extends ClutchBlockEntity {

    public int multiplierRatio = 200;
    public int multiplierRatioTop = 200;
    public int multiplierRatioBottom = 200;

    public SmartSpeedDoublerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static int minMultiplierRatio() {
        return -Config.SIMPLE_KPP_MAX.get();
    }

    public static int maxMultiplierRatio() {
        return Config.SIMPLE_KPP_MAX.get();
    }

    public static int sanitizeMultiplierRatio(int value) {
        return Mth.clamp(value, minMultiplierRatio(), maxMultiplierRatio());
    }

    @Override
    public float getRotationSpeedModifier(Direction face) {
        if (!hasSource()) {
            return 1f;
        }

        Direction sourceFacing = getSourceFacing();
        if (face == sourceFacing) {
            return 1f;
        }

        if (getLevel() == null) {
            return multiplierRatio / 100.0f;
        }
        BlockPos pos = getBlockPos();
        Direction.Axis blockAxis = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS);
        boolean hasTopSignal = false;
        boolean hasBottomSignal = false;
        
        if (blockAxis == Direction.Axis.Y) {
            int upSignal = getLevel().getSignal(pos.relative(Direction.UP), Direction.UP);
            int downSignal = getLevel().getSignal(pos.relative(Direction.DOWN), Direction.DOWN);
            int eastSignal = getLevel().getSignal(pos.relative(Direction.EAST), Direction.EAST);
            int westSignal = getLevel().getSignal(pos.relative(Direction.WEST), Direction.WEST);
            int southSignal = getLevel().getSignal(pos.relative(Direction.SOUTH), Direction.SOUTH);
            int northSignal = getLevel().getSignal(pos.relative(Direction.NORTH), Direction.NORTH);
            if (upSignal >= 15 && downSignal < 15) {
                hasTopSignal = true;
            } else if (downSignal >= 15 && upSignal < 15) {
                hasBottomSignal = true;
            } else if ((eastSignal >= 15 || southSignal >= 15) && westSignal < 15 && northSignal < 15) {
                hasTopSignal = true;
            } else if ((westSignal >= 15 || northSignal >= 15) && eastSignal < 15 && southSignal < 15) {
                hasBottomSignal = true;
            } else {
                if (downSignal >= 15) {
                    hasBottomSignal = true;
                } else if (upSignal >= 15) {
                    hasTopSignal = true;
                } else if (westSignal >= 15 || northSignal >= 15) {
                    hasBottomSignal = true;
                } else if (eastSignal >= 15 || southSignal >= 15) {
                    hasTopSignal = true;
                }
            }
        } else {
            int upSignal = getLevel().getSignal(pos.relative(Direction.UP), Direction.UP);
            int downSignal = getLevel().getSignal(pos.relative(Direction.DOWN), Direction.DOWN);
            
            Direction positiveDir, negativeDir;
            if (blockAxis == Direction.Axis.X) {
                positiveDir = Direction.SOUTH;
                negativeDir = Direction.NORTH;
            } else {
                positiveDir = Direction.EAST;
                negativeDir = Direction.WEST;
            }
            int positiveSignal = getLevel().getSignal(pos.relative(positiveDir), positiveDir);
            int negativeSignal = getLevel().getSignal(pos.relative(negativeDir), negativeDir);
            if (negativeSignal >= 15) {
                hasBottomSignal = true;
            } else if (downSignal >= 15) {
                hasBottomSignal = true;
            } else if (upSignal >= 15) {
                hasTopSignal = true;
            } else if (positiveSignal >= 15) {
                hasTopSignal = true;
            }
        }
        float modifier;
        if (hasBottomSignal) {
            modifier = multiplierRatioBottom / 100.0f;
        } else if (hasTopSignal) {
            modifier = multiplierRatioTop / 100.0f;
        } else {
            modifier = multiplierRatio / 100.0f;
        }
        return KineticSpeedLimiter.clampModifier(this, modifier);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (compound.contains("multiplier"))
            multiplierRatio = sanitizeMultiplierRatio(compound.getInt("multiplier"));
        if (compound.contains("multiplierTop"))
            multiplierRatioTop = sanitizeMultiplierRatio(compound.getInt("multiplierTop"));
        if (compound.contains("multiplierBottom"))
            multiplierRatioBottom = sanitizeMultiplierRatio(compound.getInt("multiplierBottom"));
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("multiplier", multiplierRatio);
        compound.putInt("multiplierTop", multiplierRatioTop);
        compound.putInt("multiplierBottom", multiplierRatioBottom);
    }
}
