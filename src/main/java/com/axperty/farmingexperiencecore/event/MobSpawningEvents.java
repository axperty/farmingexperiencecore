package com.axperty.farmingexperiencecore.event;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.axperty.farmingexperiencecore.config.ModConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

// Makes cave mobs not spawn on the surface and surface mobs not spawn in caves.

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)
public class MobSpawningEvents {

    @SubscribeEvent
    public static void onMobSpawnCheck(FinalizeSpawnEvent event) {
        if (event.getSpawnType() == MobSpawnType.NATURAL || event.getSpawnType() == MobSpawnType.CHUNK_GENERATION) {
            var entity = event.getEntity();
            var level = event.getLevel();
            if (level.getLevel().dimension() == Level.OVERWORLD) {
                boolean canSeeSky = level.getLevel().canSeeSky(entity.blockPosition());
                
                TagKey<EntityType<?>> caveMobsTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "cave_mobs"));
                if (ModConfig.enableCaveMobRestrictions && entity.getType().is(caveMobsTag)) {
                    if (canSeeSky || entity.getBlockY() >= 59) {
                        event.setSpawnCancelled(true);
                    }
                }
                
                TagKey<EntityType<?>> surfaceMobsTag = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "surface_mobs"));
                if (ModConfig.enableSurfaceMobRestrictions && entity.getType().is(surfaceMobsTag)) {
                    if (!canSeeSky) {
                        event.setSpawnCancelled(true);
                    }
                }
            }
        }
    }
}
