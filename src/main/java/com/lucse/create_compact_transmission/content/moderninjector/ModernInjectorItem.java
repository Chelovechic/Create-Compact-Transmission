package com.lucse.create_compact_transmission.content.moderninjector;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class ModernInjectorItem extends Item {

    private static Class<?> abstractEngineBlockEntityClass;
    private static Class<?> abstractSmallEngineBlockEntityClass;

    static {
        try {
            abstractEngineBlockEntityClass = Class.forName("com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity");
        } catch (Exception e) {
            abstractEngineBlockEntityClass = null;
        }
        try {
            abstractSmallEngineBlockEntityClass = Class.forName("com.drmangotea.tfmg.content.engines.types.AbstractSmallEngineBlockEntity");
        } catch (Exception e) {
            abstractSmallEngineBlockEntityClass = null;
        }
    }

    public ModernInjectorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide || player == null) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            return InteractionResult.PASS;
        }

        if (abstractEngineBlockEntityClass == null || !abstractEngineBlockEntityClass.isAssignableFrom(be.getClass())) {
            return InteractionResult.PASS;
        }

        try {
            BlockEntity engineTarget = resolveFuelConsumingEngine(be);
            if (engineTarget == null) {
                engineTarget = be;
            }

            if (isInstalled(engineTarget) || isInstalled(be)) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("item.create_compact_transmission.modern_injector.already_installed"), true);
                }
                return InteractionResult.FAIL;
            }

            setInstalled(engineTarget, true);
            if (engineTarget != be) {
                setInstalled(be, true);
            }
            engineTarget.setChanged();
            if (engineTarget != be) {
                be.setChanged();
            }
            level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5f, 1.2f);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("item.create_compact_transmission.modern_injector.installed"), true);
            }

            return InteractionResult.SUCCESS;

        } catch (Exception e) {
            return InteractionResult.PASS;
        }
    }

    public static boolean isInstalled(BlockEntity engineBE) {
        if (engineBE == null || abstractEngineBlockEntityClass == null) {
            return false;
        }

        try {
            BlockEntity fuelConsumingEngine = resolveFuelConsumingEngine(engineBE);
            if (fuelConsumingEngine == null) {
                fuelConsumingEngine = engineBE;
            }

            if (!abstractEngineBlockEntityClass.isAssignableFrom(fuelConsumingEngine.getClass())) {
                return false;
            }

            try {
                java.lang.reflect.Method getMethod = fuelConsumingEngine.getClass().getMethod("isModernInjectorInstalled");
                return (Boolean) getMethod.invoke(fuelConsumingEngine);
            } catch (Exception e) {
                if (fuelConsumingEngine.getLevel() == null) {
                    return false;
                }
                CompoundTag nbt = fuelConsumingEngine.saveWithFullMetadata(fuelConsumingEngine.getLevel().registryAccess());
                return nbt.getBoolean("modernInjectorInstalled");
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static void setInstalled(BlockEntity engineBE, boolean installed) {
        if (engineBE == null) {
            return;
        }

        try {
            java.lang.reflect.Method setMethod = engineBE.getClass().getMethod("setModernInjectorInstalled", boolean.class);
            setMethod.invoke(engineBE, installed);
        } catch (Exception e) {
            if (engineBE.getLevel() == null) {
                return;
            }
            CompoundTag nbt = engineBE.saveWithFullMetadata(engineBE.getLevel().registryAccess());
            nbt.putBoolean("modernInjectorInstalled", installed);
            engineBE.loadWithComponents(nbt, engineBE.getLevel().registryAccess());
        }
    }

    private static BlockEntity resolveFuelConsumingEngine(BlockEntity engineBE) {
        if (engineBE == null || abstractSmallEngineBlockEntityClass == null) {
            return engineBE;
        }
        if (!abstractSmallEngineBlockEntityClass.isAssignableFrom(engineBE.getClass())) {
            return engineBE;
        }

        try {
            java.lang.reflect.Method getControllerBEMethod = engineBE.getClass().getMethod("getControllerBE");
            Object controller = getControllerBEMethod.invoke(engineBE);
            if (controller instanceof BlockEntity blockEntity) {
                return blockEntity;
            }
        } catch (Exception ignored) {
        }
        return engineBE;
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("item.create_compact_transmission.modern_injector.tooltip"));
    }
}
