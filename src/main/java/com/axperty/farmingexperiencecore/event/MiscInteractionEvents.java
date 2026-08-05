package com.axperty.farmingexperiencecore.event;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.axperty.farmingexperiencecore.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

// Adds minecart placing, keep inventory when player dies, and throw TNT when right click.

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)
public class MiscInteractionEvents {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        if (ModConfig.enableKeepInventoryForced) {
            event.getServer().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, event.getServer());
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Item item = event.getItemStack().getItem();
        Level level = event.getLevel();
        
        if (ModConfig.enableMinecartAnywhere && item instanceof MinecartItem) {
            BlockPos pos = event.getPos();
            if (!level.getBlockState(pos).is(BlockTags.RAILS)) {
                if (!level.isClientSide()) {
                    Direction face = event.getFace();
                    BlockPos spawnPos = pos.relative(face != null ? face : Direction.UP);
                    
                    EntityType<?> type = EntityType.MINECART;
                    if (item == Items.CHEST_MINECART) type = EntityType.CHEST_MINECART;
                    else if (item == Items.FURNACE_MINECART) type = EntityType.FURNACE_MINECART;
                    else if (item == Items.TNT_MINECART) type = EntityType.TNT_MINECART;
                    else if (item == Items.HOPPER_MINECART) type = EntityType.HOPPER_MINECART;
                    
                    Entity minecart = type.create(level);
                    if (minecart != null) {
                        minecart.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                        level.addFreshEntity(minecart);
                        if (!player.isCreative()) {
                            event.getItemStack().shrink(1);
                        }
                    }
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
                return;
            }
        }
        
        if (ModConfig.enableTntThrowing && player.isCrouching() && item == Items.TNT) {
            InteractionHand otherHandType = event.getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack otherHandStack = player.getItemInHand(otherHandType);
            
            if (otherHandStack.getItem() == Items.FLINT_AND_STEEL) {
                if (!level.isClientSide()) {
                    throwTnt(player, level, event.getItemStack(), otherHandStack, otherHandType);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (ModConfig.enableTntThrowing && player.isCrouching() && event.getItemStack().getItem() == Items.TNT) {
            InteractionHand otherHandType = event.getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack otherHandStack = player.getItemInHand(otherHandType);

            if (otherHandStack.getItem() == Items.FLINT_AND_STEEL) {
                Level level = event.getLevel();
                if (!level.isClientSide()) {
                    throwTnt(player, level, event.getItemStack(), otherHandStack, otherHandType);
                }
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            }
        }
    }

    private static void throwTnt(Player player, Level level, ItemStack tntStack, ItemStack flintStack, InteractionHand flintHand) {
        PrimedTnt tnt = new PrimedTnt(level, player.getX(), player.getEyeY(), player.getZ(), player);
        tnt.setFuse(60); // 3 seconds
        Vec3 look = player.getLookAngle();
        tnt.setDeltaMovement(look.x * 1.5, look.y * 1.5, look.z * 1.5);
        level.addFreshEntity(tnt);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TNT_PRIMED, SoundSource.PLAYERS, 1.0F, 1.0F);
        if (!player.isCreative()) {
            tntStack.shrink(1);
            if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                flintStack.hurtAndBreak(1, serverLevel, serverPlayer, item -> {});
            }
        }
    }
}
