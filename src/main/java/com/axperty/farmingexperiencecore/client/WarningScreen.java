package com.axperty.farmingexperiencecore.client;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

// Displays a warning screen when the instance launches prompting the user to download the Farming Experience modpack.

@EventBusSubscriber(modid = FarmingExperienceCore.MODID, value = Dist.CLIENT)
public class WarningScreen extends Screen {

    private static boolean warningShown = false;

    public WarningScreen() {
        super(Component.literal("Farming Experience Modpack Missing"));
    }

    @SubscribeEvent
    public static void onScreenOpen(ScreenEvent.Opening event) {
        if (event.getScreen() instanceof TitleScreen && !FarmingExperienceCore.isModpackInstalled && !warningShown) {
            warningShown = true; 
            event.setNewScreen(new WarningScreen());
        }
    }

    @Override
    protected void init() {
        super.init();
        
        int buttonWidth = 160;
        int buttonHeight = 20;
        int spacing = 10;

        int startY = this.height / 2 + 40;

        int totalWidth = (buttonWidth * 2) + spacing;
        int startX = (this.width / 2) - (totalWidth / 2);

        this.addRenderableWidget(Button.builder(Component.literal("Download from CurseForge"), button -> {
            Util.getPlatform().openUri("https://www.curseforge.com/minecraft/modpacks/farming-experience");
        }).bounds(startX, startY, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(Component.literal("Download from Modrinth"), button -> {
            Util.getPlatform().openUri("https://modrinth.com/modpack/farming-experience");
        }).bounds(startX + buttonWidth + spacing, startY, buttonWidth, buttonHeight).build());

        this.addRenderableWidget(Button.builder(Component.literal("Quit Game"), button -> {
            this.minecraft.stop();
        }).bounds(this.width / 2 - buttonWidth / 2, startY + buttonHeight + spacing, buttonWidth, buttonHeight).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        guiGraphics.drawCenteredString(this.font, Component.literal("The Farming Experience modpack is not installed!").withStyle(ChatFormatting.RED), this.width / 2, this.height / 2 - 60, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, Component.literal("You are running the Core mod, but the actual Farming Experience modpack"), this.width / 2, this.height / 2 - 30, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, Component.literal("is either not installed or not installed via CurseForge/Modrinth."), this.width / 2, this.height / 2 - 15, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, Component.literal("Please download the official modpack using the links below."), this.width / 2, this.height / 2, 0xFFFFFF);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
