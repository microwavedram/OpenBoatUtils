package dev.o7moon.openboatutils.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class PlayByteBufChannel extends ByteBufChannel<ServerPlayer, ServerPlayNetworking.PlayPayloadHandler<ByteBufChannel.BytePayload>, ClientPlayNetworking.PlayPayloadHandler<ByteBufChannel.BytePayload>> {

    public PlayByteBufChannel(ResourceLocation identifier) {
        super(identifier);
    }

    public void registerCodec() {
        PayloadTypeRegistry.playS2C().register(id, codec);
        PayloadTypeRegistry.playC2S().register(id, codec);
    }

    public void registerServerHandler(ServerPlayNetworking.PlayPayloadHandler<BytePayload> handler) {
        ServerPlayNetworking.registerGlobalReceiver(id, handler);
    }

    public void registerClientHandler(ClientPlayNetworking.PlayPayloadHandler<BytePayload> handler) {
        ClientPlayNetworking.registerGlobalReceiver(id, handler);
    }

    public void sendPacketC2S(FriendlyByteBuf byteBuf) {
        ClientPlayNetworking.send(new BytePayload() {
            @Override
            public ByteBuf getData() {
                return byteBuf;
            }

            @Override
            public Type<? extends CustomPacketPayload> type() {
                return id;
            }
        });
    }

    public void sendPacketS2C(ServerPlayer player, FriendlyByteBuf byteBuf) {
        ServerPlayNetworking.send(player, new BytePayload() {
            @Override
            public ByteBuf getData() {
                return byteBuf;
            }

            @Override
            public Type<? extends CustomPacketPayload> type() {
                return id;
            }
        });
    }
}
