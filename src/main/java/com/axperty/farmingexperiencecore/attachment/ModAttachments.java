package com.axperty.farmingexperiencecore.attachment;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

// Variables for jump count, dash cooldown, and extra hearts.

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FarmingExperienceCore.MODID);

    public static final Supplier<AttachmentType<Integer>> JUMP_COUNT = ATTACHMENT_TYPES.register("jump_count",
            () -> AttachmentType.builder(() -> 0).build());

    public static final Supplier<AttachmentType<Integer>> DASH_COOLDOWN = ATTACHMENT_TYPES.register("dash_cooldown",
            () -> AttachmentType.builder(() -> 0).build());
}
