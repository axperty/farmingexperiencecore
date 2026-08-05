package com.axperty.farmingexperiencecore.network;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

// Makes the server know when the client wants to multijump.

public record MultiJumpPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MultiJumpPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "multi_jump"));
    public static final StreamCodec<ByteBuf, MultiJumpPayload> STREAM_CODEC = StreamCodec.unit(new MultiJumpPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
