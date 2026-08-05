package com.axperty.farmingexperiencecore.network;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)
public class UpdateChecker {

    // Reads from the json file in the root of the GitHub repo
    private static final String REMOTE_URL = "https://raw.githubusercontent.com/axperty/farmingexperiencecore/master/update.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                // Setup local config file
                File configDir = new File(FMLPaths.CONFIGDIR.get().toFile(), "farmingexperiencecore");
                if (!configDir.exists()) {
                    configDir.mkdirs();
                }

                File localUpdateFile = new File(configDir, "update.json");
                String localVersion = "00.00.00"; // default version

                if (!localUpdateFile.exists()) {
                    JsonObject defaultJson = new JsonObject();
                    defaultJson.addProperty("version", localVersion);
                    Files.writeString(localUpdateFile.toPath(), GSON.toJson(defaultJson));
                } else {
                    String jsonString = Files.readString(localUpdateFile.toPath());
                    JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
                    if (json.has("version")) {
                        localVersion = json.get("version").getAsString().trim();
                    }
                }

                // Fetch remote version
                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();
                        
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(REMOTE_URL))
                        .GET()
                        .build();
                        
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200) {
                    JsonObject remoteJson = JsonParser.parseString(response.body()).getAsJsonObject();
                    if (remoteJson.has("version")) {
                        String remoteVersion = remoteJson.get("version").getAsString().trim();
                        
                        // Make sure to always update the local file with the remote version when a change is detected. 
                        if (!remoteVersion.isEmpty() && remoteVersion.compareTo(localVersion) > 0) {
                            sendUpdateMessage(player, remoteVersion);
                            
                            JsonObject newLocalJson = new JsonObject();
                            newLocalJson.addProperty("version", remoteVersion);
                            Files.writeString(localUpdateFile.toPath(), GSON.toJson(newLocalJson));
                        }
                    }
                }
            } catch (Exception e) {
                LogUtils.getLogger().warn("Failed to check for Farming Experience updates: {}", e.getMessage());
            }
        });
        
        thread.setName("FarmingExperience Update Checker");
        thread.setDaemon(true);
        thread.start();
    }
    
    // Send a message to the player with the update link.
    private static void sendUpdateMessage(ServerPlayer player, String version) {
        MutableComponent message = Component.literal("New Farming Experience Update Available! " + version + " ")
                .withStyle(ChatFormatting.GREEN);

        player.sendSystemMessage(message);
    }
}
