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

    static {
        try {
            abstractEngineBlockEntityClass = Class.forName("com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity");
        } catch (Exception e) {
            abstractEngineBlockEntityClass = null;
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
            if (isInstalled(be)) {
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("item.create_compact_transmission.modern_injector.already_installed"), true);
                }
                return InteractionResult.FAIL;
            }

            try {
                java.lang.reflect.Method setMethod = be.getClass().getMethod("setModernInjectorInstalled", boolean.class);
                setMethod.invoke(be, true);
            } catch (Exception e) {
                CompoundTag nbt = be.saveWithFullMetadata();
                nbt.putBoolean("modernInjectorInstalled", true);
                be.load(nbt);
            }
            be.setChanged();
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
            if (!abstractEngineBlockEntityClass.isAssignableFrom(engineBE.getClass())) {
                return false;
            }

            try {
                java.lang.reflect.Method getMethod = engineBE.getClass().getMethod("isModernInjectorInstalled");
                return (Boolean) getMethod.invoke(engineBE);
            } catch (Exception e) {
                CompoundTag nbt = engineBE.saveWithFullMetadata();
                return nbt.getBoolean("modernInjectorInstalled");
            }
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.create_compact_transmission.modern_injector.tooltip"));
    }
}

