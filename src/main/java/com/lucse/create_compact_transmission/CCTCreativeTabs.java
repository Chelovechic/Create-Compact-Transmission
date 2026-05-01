package com.lucse.create_compact_transmission;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CCTCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateCompactTransmission.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = REGISTER.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.create_compact_transmission.main"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon(() -> CCTBlocks.COMPACT_SPEED_REGULATOR.asStack())
                    .displayItems(new RegistrateDisplayItemsGenerator(true, CCTCreativeTabs.MAIN))
                    .build());

    public static void register(IEventBus modEventBus) {
        REGISTER.register(modEventBus);
    }

    private static class RegistrateDisplayItemsGenerator implements DisplayItemsGenerator {
        private static final Predicate<Item> IS_ITEM_3D_PREDICATE;

        static {
            Predicate<Item> predicate = item -> false;
            if (CatnipServices.PLATFORM.getEnv().isClient()) {
                predicate = item -> {
                    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
                    BakedModel model = itemRenderer.getModel(new ItemStack(item), null, null, 0);
                    return model.isGui3d();
                };
            }
            IS_ITEM_3D_PREDICATE = predicate;
        }

        private final boolean addItems;
        private final DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter;

        private RegistrateDisplayItemsGenerator(boolean addItems, DeferredHolder<CreativeModeTab, CreativeModeTab> tabFilter) {
            this.addItems = addItems;
            this.tabFilter = tabFilter;
        }

        @Override
        public void accept(ItemDisplayParameters parameters, Output output) {
            Set<Item> items = new LinkedHashSet<>();
            if (addItems) {
                collectItems(items, exclusionPredicate());
            }
            collectBlocks(items, exclusionPredicate());
            if (addItems) {
                collectItems(items, exclusionPredicate().or(IS_ITEM_3D_PREDICATE));
            }

            for (Item item : items) {
                output.accept(new ItemStack(item), TabVisibility.PARENT_AND_SEARCH_TABS);
            }
        }

        private Predicate<Item> exclusionPredicate() {
            return item -> false;
        }

        private void collectBlocks(Set<Item> items, Predicate<Item> exclusionPredicate) {
            for (RegistryEntry<Block, Block> entry : CreateCompactTransmission.getRegistrate().getAll(Registries.BLOCK)) {
                if (!CreateRegistrate.isInCreativeTab(entry, tabFilter)) {
                    continue;
                }
                Item item = entry.get().asItem();
                if (item == Items.AIR || exclusionPredicate.test(item)) {
                    continue;
                }
                items.add(item);
            }
        }

        private void collectItems(Set<Item> items, Predicate<Item> exclusionPredicate) {
            for (RegistryEntry<Item, Item> entry : CreateCompactTransmission.getRegistrate().getAll(Registries.ITEM)) {
                if (!CreateRegistrate.isInCreativeTab(entry, tabFilter)) {
                    continue;
                }
                Item item = entry.get();
                if (item instanceof BlockItem || exclusionPredicate.test(item)) {
                    continue;
                }
                items.add(item);
            }
        }
    }
}
