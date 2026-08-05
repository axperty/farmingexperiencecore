package com.axperty.farmingexperiencecore.network;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.axperty.farmingexperiencecore.attachment.ModAttachments;
import com.axperty.farmingexperiencecore.config.ModConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Optional;

// Makes the client send packets to the server so the server can handle the packets.

@EventBusSubscriber(modid = FarmingExperienceCore.MODID, bus = EventBusSubscriber.Bus.MOD)
public class PacketHandler {

    private static final ResourceKey<Enchantment> DASH_KEY = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "dash"));
    private static final ResourceKey<Enchantment> MULTI_JUMP_KEY = ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "multi_jump"));

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(FarmingExperienceCore.MODID);

        registrar.playToServer(
                DashPayload.TYPE,
                DashPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> handleDash(context))
        );

        registrar.playToServer(
                MultiJumpPayload.TYPE,
                MultiJumpPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> handleMultiJump(context))
        );
    }

    private static void handleDash(IPayloadContext context) {
        Player player = context.player();
        if (player == null) return;

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty()) return;

        Optional<Holder.Reference<Enchantment>> dashHolder = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(DASH_KEY);
        if (dashHolder.isEmpty()) return;

        int dashLevel = EnchantmentHelper.getItemEnchantmentLevel(dashHolder.get(), boots);
        if (dashLevel > 0 && ModConfig.enableDash) {
            int cooldown = player.getData(ModAttachments.DASH_COOLDOWN);
            if (cooldown <= 0) {
                player.causeFoodExhaustion(0.5f);
                player.setData(ModAttachments.DASH_COOLDOWN, 100); // 2 seconds cooldown
            }
        }
    }

    private static void handleMultiJump(IPayloadContext context) {
        Player player = context.player();
        if (player == null) return;

        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (boots.isEmpty()) return;

        Optional<Holder.Reference<Enchantment>> jumpHolder = player.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(MULTI_JUMP_KEY);
        if (jumpHolder.isEmpty()) return;

        int jumpLevel = EnchantmentHelper.getItemEnchantmentLevel(jumpHolder.get(), boots);
        if (jumpLevel > 0 && ModConfig.enableMultiJump) {
            int jumps = player.getData(ModAttachments.JUMP_COUNT);
            if (jumps < jumpLevel) {
                player.setData(ModAttachments.JUMP_COUNT, jumps + 1);
                player.causeFoodExhaustion(0.2f);
            }
        }
    }
}
