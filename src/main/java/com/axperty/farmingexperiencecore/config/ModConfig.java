package com.axperty.farmingexperiencecore.config;

import net.neoforged.fml.loading.FMLPaths;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

// Makes a configuration file which allows the player to change whatever they want from this mod.

public class ModConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static File configFile;

    // Mob Rebalancing
    public static boolean enableZombieNerf = true;
    public static boolean enableSkeletonNerf = true;
    public static boolean enableCreeperNerf = true;
    public static boolean enableCaveSpiderNerf = true;
    public static boolean enableHuskBuff = true;

    // Mob Behavior
    public static boolean enableZombieLightAvoidance = true;
    public static boolean enableCatTamingBuff = true;
    public static boolean enableDisablePhantoms = true;
    public static boolean enableDisableHostileArmorDrops = true;

    // Anti-Farm Mechanics
    public static boolean enableActiveLootingOnly = true;
    public static boolean enableEndermanXpNerf = true;

    // Spawning Rules
    public static boolean enableCaveMobRestrictions = true;
    public static boolean enableSurfaceMobRestrictions = true;

    // Enchantment Mechanics
    public static boolean enableSmash = true;
    public static boolean enableDash = true;
    public static boolean enableMultiJump = true;

    // Health Mechanics
    public static boolean enableDeathPenalty = true;
    public static boolean enableGoldenAppleRestore = true;

    // Miscellaneous
    public static boolean enableKeepInventoryForced = true;
    public static boolean enableMinecartAnywhere = true;
    public static boolean enableTntThrowing = true;
    public static boolean enableCauldronWashing = true;

    public static void init() {
        File configDir = new File(FMLPaths.CONFIGDIR.get().toFile(), "farmingexperiencecore");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        configFile = new File(configDir, "main.properties");

        if (configFile.exists()) {
            loadConfig(configFile);
            saveConfig();
        } else {
            saveConfig();
        }
    }

    private static void loadConfig(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim();
                    boolean value = Boolean.parseBoolean(parts[1].trim());

                    switch(key) {
                        case "enableZombieNerf": enableZombieNerf = value; break;
                        case "enableSkeletonNerf": enableSkeletonNerf = value; break;
                        case "enableCreeperNerf": enableCreeperNerf = value; break;
                        case "enableCaveSpiderNerf": enableCaveSpiderNerf = value; break;
                        case "enableHuskBuff": enableHuskBuff = value; break;
                        
                        case "enableZombieLightAvoidance": enableZombieLightAvoidance = value; break;
                        case "enableCatTamingBuff": enableCatTamingBuff = value; break;
                        case "enableDisablePhantoms": enableDisablePhantoms = value; break;
                        case "enableDisableHostileArmorDrops": enableDisableHostileArmorDrops = value; break;
                        
                        case "enableActiveLootingOnly": enableActiveLootingOnly = value; break;
                        case "enableEndermanXpNerf": enableEndermanXpNerf = value; break;
                        
                        case "enableCaveMobRestrictions": enableCaveMobRestrictions = value; break;
                        case "enableSurfaceMobRestrictions": enableSurfaceMobRestrictions = value; break;
                        
                        case "enableSmash": enableSmash = value; break;
                        case "enableDash": enableDash = value; break;
                        case "enableMultiJump": enableMultiJump = value; break;
                        
                        case "enableDeathPenalty": enableDeathPenalty = value; break;
                        case "enableGoldenAppleRestore": enableGoldenAppleRestore = value; break;
                        
                        case "enableKeepInventoryForced": enableKeepInventoryForced = value; break;
                        case "enableMinecartAnywhere": enableMinecartAnywhere = value; break;
                        case "enableTntThrowing": enableTntThrowing = value; break;
                        case "enableCauldronWashing": enableCauldronWashing = value; break;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load Farming Experience Core config!", e);
        }
    }

    public static void saveConfig() {
        if (configFile == null) return;
        try (PrintWriter writer = new PrintWriter(configFile)) {
            writer.println("# ----- Farming Experience Core Config -----");
            writer.println("# True means the setting will be turned on.");
            writer.println("# False means the setting will be turned off.");
            writer.println("# Type the desired value below using lowercase.");
            writer.println("# ------------------------------------------");
            writer.println();

            writer.println("# ----- Mob Rebalancing -----");
            writeProperty(writer, "Reduce zombie health to 10 HP and increase speed. Default: true", "enableZombieNerf", enableZombieNerf);
            writeProperty(writer, "Reduce skeleton health to 10 HP. Default: true", "enableSkeletonNerf", enableSkeletonNerf);
            writeProperty(writer, "Reduce creeper health to 10 HP. Default: true", "enableCreeperNerf", enableCreeperNerf);
            writeProperty(writer, "Reduce cave spider health to 4 HP and increase speed. Default: true", "enableCaveSpiderNerf", enableCaveSpiderNerf);
            writeProperty(writer, "Increase husk zombie attack damage and speed. Default: true", "enableHuskBuff", enableHuskBuff);

            writer.println("# ----- Mob Behavior -----");
            writeProperty(writer, "Zombies avoid light sources. Default: true", "enableZombieLightAvoidance", enableZombieLightAvoidance);
            writeProperty(writer, "Cats will no longer run away from players. Default: true", "enableCatTamingBuff", enableCatTamingBuff);
            writeProperty(writer, "Prevent phantoms from spawning. Default: true", "enableDisablePhantoms", enableDisablePhantoms);
            writeProperty(writer, "Mundane hostile mobs no longer have a chance to drop their equipped armor or weapons. Default: true", "enableDisableHostileArmorDrops", enableDisableHostileArmorDrops);

            writer.println("# ----- Anti-Mob Farm Mechanics -----");
            writeProperty(writer, "Mobs will only drop loot and XP if killed by a player (disables passive mob farms). Default: true", "enableActiveLootingOnly", enableActiveLootingOnly);
            writeProperty(writer, "Endermen drop significantly less XP to discourage enderman XP farms. Default: true", "enableEndermanXpNerf", enableEndermanXpNerf);

            writer.println("# ----- Spawning Rules -----");
            writeProperty(writer, "Creepers and spiders only spawn naturally below Y-level 59 without sky access. Default: true", "enableCaveMobRestrictions", enableCaveMobRestrictions);
            writeProperty(writer, "Zombies, skeletons, endermen, and witches only spawn naturally if they have direct access to the sky. Default: true", "enableSurfaceMobRestrictions", enableSurfaceMobRestrictions);

            writer.println("# ----- Enchantment Mechanics -----");
            writeProperty(writer, "Enable the Smash enchantment for the mace. Default: true", "enableSmash", enableSmash);
            writeProperty(writer, "Enable the Dash enchantment for boots. Default: true", "enableDash", enableDash);
            writeProperty(writer, "Enable the Multijump enchantment for boots. Default: true", "enableMultiJump", enableMultiJump);

            writer.println("# ----- Health Mechanics -----");
            writeProperty(writer, "Dying permanently reduces the player's maximum health by 1 heart (down to a minimum of 1 heart). Default: true", "enableDeathPenalty", enableDeathPenalty);
            writeProperty(writer, "Eating golden apples or enchanted golden apples restores 1 lost heart of maximum health. Default: true", "enableGoldenAppleRestore", enableGoldenAppleRestore);

            writer.println("# ----- Miscellaneous -----");
            writeProperty(writer, "Keep Inventory is permanently forced to true upon world start. Default: true", "enableKeepInventoryForced", enableKeepInventoryForced);
            writeProperty(writer, "Minecarts can be placed on any block, not just rails. Default: true", "enableMinecartAnywhere", enableMinecartAnywhere);
            writeProperty(writer, "Crouching and right-clicking TNT while holding flint & steel throws it. Default: true", "enableTntThrowing", enableTntThrowing);
            writeProperty(writer, "Items crafted with dyes can be placed in water cauldrons to wash them back to their base color. Default: true", "enableCauldronWashing", enableCauldronWashing);
        } catch (Exception e) {
            LOGGER.error("Failed to save Farming Experience config!", e);
        }
    }

    private static void writeProperty(PrintWriter writer, String comment, String key, boolean value) {
        writer.println("# " + comment);
        writer.println(key + "=" + value);
        writer.println();
    }
}
