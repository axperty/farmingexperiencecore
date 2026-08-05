package com.axperty.farmingexperiencecore.event;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.axperty.farmingexperiencecore.entity.ZombieAvoidLightGoal;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)

// Disables iron golems and phantoms completely by preventing them from joining the world and makes zombies avoid light.

public class EntityEvents {
    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {

        // Disable phantoms.

        if (event.getEntity() instanceof Phantom) {
            event.setCanceled(true);
            return;
        }

        // Give less health to mobs.
        if (event.getEntity() instanceof Zombie zombie) {
            zombie.goalSelector.addGoal(0, new ZombieAvoidLightGoal(zombie, 1.2));
            if (!event.getLevel().isClientSide()) {
                var speedAttr = zombie.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedAttr != null) speedAttr.setBaseValue(0.3105);
                
                var hpAttr = zombie.getAttribute(Attributes.MAX_HEALTH);
                if (hpAttr != null) {
                    hpAttr.setBaseValue(10.0);
                    if (zombie.getHealth() > 10.0f) zombie.setHealth(10.0f);
                }
            }
        }

        if (event.getEntity() instanceof Skeleton skeleton) {
            if (!event.getLevel().isClientSide()) {
                var hpAttr = skeleton.getAttribute(Attributes.MAX_HEALTH);
                if (hpAttr != null) {
                    hpAttr.setBaseValue(10.0);
                    if (skeleton.getHealth() > 10.0f) skeleton.setHealth(10.0f);
                }
            }
        }

        if (event.getEntity() instanceof Creeper creeper) {
            if (!event.getLevel().isClientSide()) {
                var hpAttr = creeper.getAttribute(Attributes.MAX_HEALTH);
                if (hpAttr != null) {
                    hpAttr.setBaseValue(10.0);
                    if (creeper.getHealth() > 10.0f) creeper.setHealth(10.0f);
                }
            }
        }

        if (event.getEntity() instanceof CaveSpider caveSpider) {
            if (!event.getLevel().isClientSide()) {
                var hpAttr = caveSpider.getAttribute(Attributes.MAX_HEALTH);
                if (hpAttr != null) {
                    hpAttr.setBaseValue(4.0);
                    if (caveSpider.getHealth() > 4.0f) caveSpider.setHealth(4.0f);
                }
            }
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
                    if (canSeeSky || entity.getBlockY() >= 59) {
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

    // Mobs only drop items and XP if killed directly by a player
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
        if (event.getEntity() instanceof Mob) {
            if (event.getAttackingPlayer() == null) {
                event.setDroppedExperience(0);
                event.setCanceled(true);
            }
        }
    }
}
