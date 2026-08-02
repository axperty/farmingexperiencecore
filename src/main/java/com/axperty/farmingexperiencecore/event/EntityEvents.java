package com.axperty.farmingexperiencecore.event;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.axperty.farmingexperiencecore.entity.ZombieAvoidLightGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)

// Disables iron golems and phantoms completely by preventing them from joining the world and makes zombies avoid light.

public class EntityEvents {
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof IronGolem) {
            event.setCanceled(true);
            return;
        }

        if (event.getEntity() instanceof Phantom) {
            event.setCanceled(true);
            return;
        }

        if (event.getEntity() instanceof Zombie zombie) {
            zombie.goalSelector.addGoal(0, new ZombieAvoidLightGoal(zombie, 1.2));
        }
    }
}
