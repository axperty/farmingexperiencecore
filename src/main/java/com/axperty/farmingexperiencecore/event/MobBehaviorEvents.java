package com.axperty.farmingexperiencecore.event;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.axperty.farmingexperiencecore.entity.ZombieAvoidLightGoal;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Husk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)
public class MobBehaviorEvents {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof IronGolem || event.getEntity() instanceof Phantom) {
            event.setCanceled(true);
            return;
        }

        if (!event.getLevel().isClientSide() && event.getEntity() instanceof Mob mob) {
            TagKey<EntityType<?>> mundaneTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "mundane_hostiles"));
            if (mob.getType().is(mundaneTag)) {
                mob.setDropChance(EquipmentSlot.HEAD, 0.0f);
                mob.setDropChance(EquipmentSlot.CHEST, 0.0f);
                mob.setDropChance(EquipmentSlot.LEGS, 0.0f);
                mob.setDropChance(EquipmentSlot.FEET, 0.0f);
                mob.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
            }
        }

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
                
                var stepAttr = zombie.getAttribute(Attributes.STEP_HEIGHT);
                if (stepAttr != null) stepAttr.setBaseValue(1.0);
            }
        }

        if (event.getEntity() instanceof Husk husk) {
            if (!event.getLevel().isClientSide()) {
                var speedAttr = husk.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedAttr != null) speedAttr.setBaseValue(0.28);
                var dmgAttr = husk.getAttribute(Attributes.ATTACK_DAMAGE);
                if (dmgAttr != null) dmgAttr.setBaseValue(7.0);
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
                var speedAttr = caveSpider.getAttribute(Attributes.MOVEMENT_SPEED);
                if (speedAttr != null) speedAttr.setBaseValue(0.4);
            }
        }

        if (event.getEntity() instanceof Cat cat) {
            cat.goalSelector.removeAllGoals(goal -> goal instanceof AvoidEntityGoal);
        }
    }
}
