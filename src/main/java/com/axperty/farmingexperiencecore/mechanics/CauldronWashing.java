package com.axperty.farmingexperiencecore.mechanics;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.axperty.farmingexperiencecore.config.ModConfig;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.core.component.DataComponents;

@EventBusSubscriber(modid = FarmingExperienceCore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CauldronWashing {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (DyeColor color : DyeColor.values()) {
                String colorName = color.getName();

                if (color != DyeColor.WHITE) {
                    registerWashable(colorName + "_wool", Items.WHITE_WOOL);
                    registerWashable(colorName + "_carpet", Items.WHITE_CARPET);
                    registerWashable(colorName + "_bed", Items.WHITE_BED);
                    registerWashable(colorName + "_concrete", Items.WHITE_CONCRETE);
                    registerWashable(colorName + "_concrete_powder", Items.WHITE_CONCRETE_POWDER);
                }
                
                registerWashable(colorName + "_terracotta", Items.TERRACOTTA);
                registerWashable(colorName + "_stained_glass", Items.GLASS);
                registerWashable(colorName + "_stained_glass_pane", Items.GLASS_PANE);
                registerWashable(colorName + "_candle", Items.CANDLE);
            }
        });
    }

    private static void registerWashable(String itemName, Item baseItem) {
        Item coloredItem = BuiltInRegistries.ITEM.get(ResourceLocation.withDefaultNamespace(itemName));
        if (coloredItem != Items.AIR) {
            CauldronInteraction.WATER.map().put(coloredItem, (state, level, pos, player, hand, stack) -> {
                if (!ModConfig.enableCauldronWashing) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                if (!level.isClientSide) {
                    ItemStack washedStack = new ItemStack(baseItem);
                    if (stack.has(DataComponents.CUSTOM_NAME)) {
                        washedStack.set(DataComponents.CUSTOM_NAME, stack.get(DataComponents.CUSTOM_NAME));
                    }
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, washedStack));
                    LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            });
        }
    }
}
