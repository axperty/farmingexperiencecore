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
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;

@EventBusSubscriber(modid = FarmingExperienceCore.MODID)
public class UpdateChecker {

    private static final String REMOTE_URL = "https://raw.githubusercontent.com/axperty/farmingexperiencecore/main/modpack/UPDATE";
    
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                File gameDir = FMLPaths.GAMEDIR.get().toFile();
                File localUpdateFile = new File(gameDir, "modpack/UPDATE");
                
                String localVersion = "";
                if (localUpdateFile.exists()) {
                    localVersion = Files.readString(localUpdateFile.toPath()).trim();
                }

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

                    if (!remoteVersion.isEmpty() && !remoteVersion.equals(localVersion)) {
                        sendUpdateMessage(player);
                    }
                }
            } catch (IOException | InterruptedException e) {
                LogUtils.getLogger().warn("Failed to check for Farming Experience updates: {}", e.getMessage());
            }
        });
        
        thread.setName("Farming Experience Update Checker");
        thread.setDaemon(true);
        thread.start();
    }
    
    private static void sendUpdateMessage(ServerPlayer player) {
        MutableComponent title = Component.literal("New Farming Experience Update Available! ")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
                
        MutableComponent instructions = Component.literal("Please update the instance inside your launcher.")
                .withStyle(ChatFormatting.YELLOW);

        player.sendSystemMessage(Component.literal(""));
        player.sendSystemMessage(title);
        player.sendSystemMessage(instructions);
        player.sendSystemMessage(Component.literal(""));
    }
}
