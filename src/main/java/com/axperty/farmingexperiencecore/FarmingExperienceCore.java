package com.axperty.farmingexperiencecore;

import com.axperty.farmingexperiencecore.attachment.ModAttachments;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

// Checks if the player is running the Farming Experience modpack.

@Mod(FarmingExperienceCore.MODID)
public class FarmingExperienceCore {
    public static final String MODID = "farmingexperiencecore";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static boolean isModpackInstalled = true;

    public FarmingExperienceCore(IEventBus modEventBus) {
        checkModpackInstallation();
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        LOGGER.info("Farming Experience Core loaded");
    }

    private void checkModpackInstallation() {
        File gameDir = FMLPaths.GAMEDIR.get().toFile();
        File curseForgeManifest = new File(gameDir, "minecraftinstance.json");
        File manifest = new File(gameDir, "manifest.json");
        File modrinthIndex = new File(gameDir, "modrinth.index.json");
        
        boolean found = false;
        Gson gson = new Gson();
        
        File[] filesToCheck = { curseForgeManifest, manifest, modrinthIndex };
        
        for (File file : filesToCheck) {
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    if (json != null) {
                        String name = "";
                        if (json.has("name")) {
                            name = json.get("name").getAsString().toLowerCase();
                        } else if (json.has("manifest")) {
                            JsonObject manifestObj = json.getAsJsonObject("manifest");
                            if (manifestObj.has("name")) {
                                name = manifestObj.get("name").getAsString().toLowerCase();
                            }
                        }
                        
                        if (name.contains("farming experience") || name.contains("farmingexperiencecore")) {
                            found = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        isModpackInstalled = found;
    }
}
