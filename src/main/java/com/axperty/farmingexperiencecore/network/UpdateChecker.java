package com.axperty.farmingexperiencecore.network;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
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

    // Reads from the text file in the root of the GitHub repo
    private static final String REMOTE_URL = "https://raw.githubusercontent.com/axperty/farmingexperiencecore/main/UPDATE";
    
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

                File localUpdateFile = new File(configDir, "update.txt");
                String localVersion = "00/00/00"; // default version

                if (!localUpdateFile.exists()) {
                    Files.writeString(localUpdateFile.toPath(), localVersion);
                } else {
                    localVersion = Files.readString(localUpdateFile.toPath()).trim();
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
                    String remoteVersion = response.body().trim();
                    
                    // Simple string comparison works well for YY/MM/DD or YYYY-MM-DD
                    if (!remoteVersion.isEmpty() && remoteVersion.compareTo(localVersion) > 0) {
                        sendUpdateMessage(player, remoteVersion);
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
    
    private static void sendUpdateMessage(ServerPlayer player, String version) {
        // A single-line, non-intrusive message
        MutableComponent message = Component.literal("Modpack Update Available: " + version + " ")
                .withStyle(ChatFormatting.GREEN)
                .append(Component.literal("[Update via Launcher]")
                    .withStyle(style -> style
                        .withColor(ChatFormatting.GOLD)
                        .withUnderlined(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/modpack/farming-experience"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Open Modrinth to update")))
                    )
                );

        player.sendSystemMessage(message);
    }
}
