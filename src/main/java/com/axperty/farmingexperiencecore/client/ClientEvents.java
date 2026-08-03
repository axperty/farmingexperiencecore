package com.axperty.farmingexperiencecore.client;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.axperty.farmingexperiencecore.network.DashPayload;
import com.axperty.farmingexperiencecore.network.MultiJumpPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

@EventBusSubscriber(modid = FarmingExperienceCore.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientEvents {

    private static final ResourceKey<Enchantment> DASH_KEY = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "dash"));
    private static final ResourceKey<Enchantment> MULTI_JUMP_KEY = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "multi_jump"));

    private static int jumpCount = 0;
    private static int dashCooldown = 0;
    private static boolean wasJumpKeyDown = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.isPaused()) {
            return;
        }

        if (dashCooldown > 0) {
            dashCooldown--;
        }

        if (player.onGround()) {
            jumpCount = 0;
        }

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!boots.isEmpty()) {
            handleDash(player, boots);
            handleMultiJump(minecraft, player, boots);
        }
    }

    private static void handleDash(LocalPlayer player, ItemStack boots) {
        while (KeyBindings.DASH_KEY.consumeClick()) {
            if (dashCooldown <= 0) {
                Optional<Holder.Reference<Enchantment>> dashHolder = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(DASH_KEY);
                if (dashHolder.isPresent()) {
                    int dashLevel = EnchantmentHelper.getItemEnchantmentLevel(dashHolder.get(), boots);
                    if (dashLevel > 0) {
                        Vec3 look = player.getLookAngle();
                        Vec3 dashVec = new Vec3(look.x, 0.0, look.z).normalize().scale(1.5).add(0, 0.2, 0);
                        player.setDeltaMovement(dashVec.x, dashVec.y, dashVec.z);
                        player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_1, SoundSource.PLAYERS, 1.0f, 1.0f);
                        PacketDistributor.sendToServer(new DashPayload());
                        dashCooldown = 100; // 5 seconds
                    }
                }
            }
        }
    }

    private static void handleMultiJump(Minecraft minecraft, LocalPlayer player, ItemStack boots) {
        boolean isJumpKeyDown = minecraft.options.keyJump.isDown();
        
        if (isJumpKeyDown && !wasJumpKeyDown) {
            if (!player.onGround() && !player.isFallFlying() && !player.isInWater() && !player.isInLava()) {
                Optional<Holder.Reference<Enchantment>> jumpHolder = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(MULTI_JUMP_KEY);
                if (jumpHolder.isPresent()) {
                    int maxJumps = EnchantmentHelper.getItemEnchantmentLevel(jumpHolder.get(), boots);
                    if (jumpCount < maxJumps) {
                        jumpCount++;
                        Vec3 movement = player.getDeltaMovement();
                        player.setDeltaMovement(movement.x, 0.5, movement.z);
                        player.fallDistance = 0.0f; // Reset fall distance to prevent damage
                        player.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 1.0f, 1.5f);
                        PacketDistributor.sendToServer(new MultiJumpPayload());
                    }
                }
            }
        }
        
        wasJumpKeyDown = isJumpKeyDown;
    }
}
