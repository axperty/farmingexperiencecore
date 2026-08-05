package com.axperty.farmingexperiencecore.client;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

// Adds keybinds. 

@EventBusSubscriber(modid = FarmingExperienceCore.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {
    public static final KeyMapping DASH_KEY = new KeyMapping(
            "key.farmingexperiencecore.dash",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.categories.farmingexperiencecore"
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(DASH_KEY);
    }
}
