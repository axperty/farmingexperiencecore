package com.axperty.farmingexperiencecore.event;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.axperty.farmingexperiencecore.entity.ZombieAvoidLightGoal;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

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

        if (event.getEntity() instanceof Cat cat) {
            cat.goalSelector.removeAllGoals(goal -> goal instanceof AvoidEntityGoal);
        }
    }

    // Checks where mobs spawn.
    @SubscribeEvent
    public static void onMobSpawnCheck(FinalizeSpawnEvent event) {
        if (event.getSpawnType() == MobSpawnType.NATURAL || event.getSpawnType() == MobSpawnType.CHUNK_GENERATION) {
            var entity = event.getEntity();
            var level = event.getLevel();
            if (level.getLevel().dimension() == Level.OVERWORLD) {
                boolean canSeeSky = level.getLevel().canSeeSky(entity.blockPosition());
                
                TagKey<EntityType<?>> caveMobsTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "cave_mobs"));
                if (entity.getType().is(caveMobsTag)) {
                    if (canSeeSky) {
                        event.setSpawnCancelled(true);
                    }
                }
                
                TagKey<EntityType<?>> surfaceMobsTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "surface_mobs"));
                if (entity.getType().is(surfaceMobsTag)) {
                    if (!canSeeSky) {
                        event.setSpawnCancelled(true);
                    }
                }
            }
        }
    }
}
