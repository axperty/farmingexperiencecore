package com.axperty.farmingexperiencecore.client.gui;

import com.axperty.farmingexperiencecore.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

// Makes a config screen for Farming Experience Core.
// This will be used to configure the mod.

public class ClothConfigScreen {

    public static Screen build(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Farming Experience Core Config"));

        builder.setSavingRunnable(() -> {
            ModConfig.saveConfig();
        });

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Category: Mob Rebalancing
        ConfigCategory mobRebalancing = builder.getOrCreateCategory(Component.literal("Mob Rebalancing"));
        mobRebalancing.addEntry(entryBuilder.startBooleanToggle(Component.literal("Zombie Nerf"), ModConfig.enableZombieNerf)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Reduce zombie health to 10 HP and increase speed."))
                .setSaveConsumer(newValue -> ModConfig.enableZombieNerf = newValue)
                .build());
        mobRebalancing.addEntry(entryBuilder.startBooleanToggle(Component.literal("Skeleton Nerf"), ModConfig.enableSkeletonNerf)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Reduce skeleton health to 10 HP."))
                .setSaveConsumer(newValue -> ModConfig.enableSkeletonNerf = newValue)
                .build());
        mobRebalancing.addEntry(entryBuilder.startBooleanToggle(Component.literal("Creeper Nerf"), ModConfig.enableCreeperNerf)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Reduce creeper health to 10 HP."))
                .setSaveConsumer(newValue -> ModConfig.enableCreeperNerf = newValue)
                .build());
        mobRebalancing.addEntry(entryBuilder.startBooleanToggle(Component.literal("Cave Spider Nerf"), ModConfig.enableCaveSpiderNerf)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Reduce cave spider health to 4 HP and increase speed."))
                .setSaveConsumer(newValue -> ModConfig.enableCaveSpiderNerf = newValue)
                .build());
        mobRebalancing.addEntry(entryBuilder.startBooleanToggle(Component.literal("Husk Buff"), ModConfig.enableHuskBuff)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Increase husk zombie attack damage and speed."))
                .setSaveConsumer(newValue -> ModConfig.enableHuskBuff = newValue)
                .build());

        // Category: Mob Behavior
        ConfigCategory mobBehavior = builder.getOrCreateCategory(Component.literal("Mob Behavior"));
        mobBehavior.addEntry(entryBuilder.startBooleanToggle(Component.literal("Zombie Light Avoidance"), ModConfig.enableZombieLightAvoidance)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Zombies avoid any light sources."))
                .setSaveConsumer(newValue -> ModConfig.enableZombieLightAvoidance = newValue)
                .build());
        mobBehavior.addEntry(entryBuilder.startBooleanToggle(Component.literal("Cats Don't Run Away"), ModConfig.enableCatTamingBuff)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Cats will no longer run away from players."))
                .setSaveConsumer(newValue -> ModConfig.enableCatTamingBuff = newValue)
                .build());
        mobBehavior.addEntry(entryBuilder.startBooleanToggle(Component.literal("Disable Phantoms"), ModConfig.enableDisablePhantoms)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Prevent phantoms from spawning."))
                .setSaveConsumer(newValue -> ModConfig.enableDisablePhantoms = newValue)
                .build());
        mobBehavior.addEntry(entryBuilder.startBooleanToggle(Component.literal("Disable Hostile Armor Drops"), ModConfig.enableDisableHostileArmorDrops)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Mundane hostile mobs no longer have a chance to drop their equipped armor or weapons."))
                .setSaveConsumer(newValue -> ModConfig.enableDisableHostileArmorDrops = newValue)
                .build());

        // Category: Anti-Farm Mechanics
        ConfigCategory antiFarm = builder.getOrCreateCategory(Component.literal("Anti-Farm Mechanics"));
        antiFarm.addEntry(entryBuilder.startBooleanToggle(Component.literal("Active Looting Only"), ModConfig.enableActiveLootingOnly)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Mobs will only drop loot and XP if killed by a player."))
                .setSaveConsumer(newValue -> ModConfig.enableActiveLootingOnly = newValue)
                .build());
        antiFarm.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enderman XP Nerf"), ModConfig.enableEndermanXpNerf)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Endermen drop significantly less XP to discourage enderman XP farms."))
                .setSaveConsumer(newValue -> ModConfig.enableEndermanXpNerf = newValue)
                .build());

        // Category: Spawning Rules
        ConfigCategory spawningRules = builder.getOrCreateCategory(Component.literal("Spawning Rules"));
        spawningRules.addEntry(entryBuilder.startBooleanToggle(Component.literal("Cave Mob Restrictions"), ModConfig.enableCaveMobRestrictions)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Creepers and spiders only spawn naturally below Y-level 59 without sky access."))
                .setSaveConsumer(newValue -> ModConfig.enableCaveMobRestrictions = newValue)
                .build());
        spawningRules.addEntry(entryBuilder.startBooleanToggle(Component.literal("Surface Mob Restrictions"), ModConfig.enableSurfaceMobRestrictions)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Zombies, skeletons, endermen, and witches only spawn naturally if they have direct access to the sky."))
                .setSaveConsumer(newValue -> ModConfig.enableSurfaceMobRestrictions = newValue)
                .build());

        // Category: Enchantment Mechanics
        ConfigCategory enchantmentMechanics = builder.getOrCreateCategory(Component.literal("Enchantment Mechanics"));
        enchantmentMechanics.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Smash"), ModConfig.enableSmash)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Enable the Smash enchantment for the mace."))
                .setSaveConsumer(newValue -> ModConfig.enableSmash = newValue)
                .build());
        enchantmentMechanics.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Dash"), ModConfig.enableDash)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Enable the Dash enchantment for boots."))
                .setSaveConsumer(newValue -> ModConfig.enableDash = newValue)
                .build());
        enchantmentMechanics.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable MultiJump"), ModConfig.enableMultiJump)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Enable the Multijump enchantment for boots."))
                .setSaveConsumer(newValue -> ModConfig.enableMultiJump = newValue)
                .build());

        // Category: Health Mechanics
        ConfigCategory healthMechanics = builder.getOrCreateCategory(Component.literal("Health Mechanics"));
        healthMechanics.addEntry(entryBuilder.startBooleanToggle(Component.literal("Death Penalty"), ModConfig.enableDeathPenalty)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Dying permanently reduces the player's maximum health by 1 heart (down to a minimum of 1 heart)."))
                .setSaveConsumer(newValue -> ModConfig.enableDeathPenalty = newValue)
                .build());
        healthMechanics.addEntry(entryBuilder.startBooleanToggle(Component.literal("Golden Apple Restore"), ModConfig.enableGoldenAppleRestore)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Eating golden apples or enchanted golden apples restores 1 lost heart of maximum health."))
                .setSaveConsumer(newValue -> ModConfig.enableGoldenAppleRestore = newValue)
                .build());

        // Category: Miscellaneous
        ConfigCategory miscellaneous = builder.getOrCreateCategory(Component.literal("Miscellaneous"));
        miscellaneous.addEntry(entryBuilder.startBooleanToggle(Component.literal("Keep Inventory Forced"), ModConfig.enableKeepInventoryForced)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Keep Inventory is permanently forced to true upon world start."))
                .setSaveConsumer(newValue -> ModConfig.enableKeepInventoryForced = newValue)
                .build());
        miscellaneous.addEntry(entryBuilder.startBooleanToggle(Component.literal("Minecart Anywhere"), ModConfig.enableMinecartAnywhere)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Minecarts can be placed on any block, not just rails."))
                .setSaveConsumer(newValue -> ModConfig.enableMinecartAnywhere = newValue)
                .build());
        miscellaneous.addEntry(entryBuilder.startBooleanToggle(Component.literal("TNT Throwing"), ModConfig.enableTntThrowing)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Crouching and right-clicking TNT while holding flint & steel throws it."))
                .setSaveConsumer(newValue -> ModConfig.enableTntThrowing = newValue)
                .build());

        return builder.build();
    }
}
