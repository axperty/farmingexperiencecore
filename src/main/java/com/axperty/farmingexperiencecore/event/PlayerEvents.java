package com.axperty.farmingexperiencecore.event;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import com.axperty.farmingexperiencecore.attachment.ModAttachments;
import net.minecraft.world.level.Level;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionResult;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)
public class PlayerEvents {

    private static final ResourceKey<Enchantment> SMASH_KEY = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "smash"));
    private static boolean isSmashing = false;

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide()) {
            if (event.getDistance() > 3.0f) {
                ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (mainHand.is(Items.MACE)) {
                    java.util.Optional<Holder.Reference<Enchantment>> smashHolder = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(SMASH_KEY);
                    if (smashHolder.isPresent()) {
                        int smashLevel = EnchantmentHelper.getItemEnchantmentLevel(smashHolder.get(), mainHand);
                        if (smashLevel > 0) {
                            event.setDamageMultiplier(0.0f);
                            isSmashing = true;
                            float radius = 1.0f + (smashLevel * 0.5f);
                            player.level().explode(player, player.getX(), player.getY(), player.getZ(), radius, Level.ExplosionInteraction.BLOCK);
                            isSmashing = false;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (isSmashing && event.getEntity() instanceof Player) {
            if (event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
                event.setNewDamage(0.0f);
            }
        }
    }

    // Makes the player not lose their inventory.
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        event.getServer().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            int dashCooldown = player.getData(ModAttachments.DASH_COOLDOWN);
            if (dashCooldown > 0) {
                player.setData(ModAttachments.DASH_COOLDOWN, dashCooldown - 1);
            }
            if (player.onGround()) {
                player.setData(ModAttachments.JUMP_COUNT, 0);
            }
        }
    }

    // Makes the player lose a heart if they die and gives it back if the player eats a golden apple.
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            Player player = event.getEntity();
            Player original = event.getOriginal();

            var attr = player.getAttribute(Attributes.MAX_HEALTH);
            if (attr != null) {
                var originalAttr = original.getAttribute(Attributes.MAX_HEALTH);
                double currentMax = originalAttr != null ? originalAttr.getBaseValue() : 20.0;
                double newMax = Math.max(2.0, currentMax - 2.0);
                attr.setBaseValue(newMax);
                if (player.getHealth() > newMax) {
                    player.setHealth((float) newMax);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onItemEaten(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            var item = event.getItem().getItem();
            if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
                var attr = player.getAttribute(Attributes.MAX_HEALTH);
                if (attr != null) {
                    double currentMax = attr.getBaseValue();
                    if (currentMax < 20.0) {
                        double newMax = Math.min(20.0, currentMax + 2.0);
                        attr.setBaseValue(newMax);
                    }
                }
            }
        }
    }
    
    // Makes minecarts placeable anywhere.
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Item item = event.getItemStack().getItem();
        Level level = event.getLevel();
        
        if (item instanceof MinecartItem) {
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
        
        // Makes the player throw TNT when they crouch and right click a block with TNT in their hand.
        if (player.isCrouching() && item == Items.TNT) {
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
        if (player.isCrouching() && event.getItemStack().getItem() == Items.TNT) {
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
