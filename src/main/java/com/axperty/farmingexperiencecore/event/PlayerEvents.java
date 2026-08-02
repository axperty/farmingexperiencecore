package com.axperty.farmingexperiencecore.event;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

// Makes the player lose a heart if they die and give it back if the player eats a golden apple.

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)
public class PlayerEvents {
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        event.getServer().getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, event.getServer());
    }

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
}
