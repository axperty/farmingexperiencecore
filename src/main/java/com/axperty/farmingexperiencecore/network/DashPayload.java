package com.axperty.farmingexperiencecore.network;

import com.axperty.farmingexperiencecore.FarmingExperienceCore;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record DashPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DashPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FarmingExperienceCore.MODID, "dash"));
    public static final StreamCodec<ByteBuf, DashPayload> STREAM_CODEC = StreamCodec.unit(new DashPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
