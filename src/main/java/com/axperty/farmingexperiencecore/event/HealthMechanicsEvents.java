package com.axperty.farmingexperiencecore.event;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.axperty.farmingexperiencecore.config.ModConfig;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

// Makes golden apples give extra max health and makes you lose max health when you die.

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)
public class HealthMechanicsEvents {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath() && ModConfig.enableDeathPenalty) {
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
        if (event.getEntity() instanceof Player player && ModConfig.enableGoldenAppleRestore) {
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
