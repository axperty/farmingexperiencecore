package com.axperty.farmingexperiencecore.event;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)
public class AntiFarmEvents {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntity() instanceof Mob) {
            if (!(event.getSource().getEntity() instanceof Player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getEntity() instanceof Mob mob) {
            if (event.getAttackingPlayer() == null) {
                event.setDroppedExperience(0);
                event.setCanceled(true);
            } else if (mob instanceof EnderMan) {
                // Nerf Enderman XP to 2 to discourage XP farms
                event.setDroppedExperience(2);
            }
        }
    }
}
