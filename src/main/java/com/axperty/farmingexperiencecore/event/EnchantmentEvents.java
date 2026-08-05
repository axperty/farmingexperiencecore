package com.axperty.farmingexperiencecore.event;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.axperty.farmingexperiencecore.attachment.ModAttachments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

// Makes enchantments work.

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)
public class EnchantmentEvents {

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
                            float radius = 1.5f + (smashLevel * 0.75f);
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
}
