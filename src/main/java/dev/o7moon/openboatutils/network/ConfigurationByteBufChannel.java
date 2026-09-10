package dev.o7moon.openboatutils.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

public class ConfigurationByteBufChannel extends ByteBufChannel<ServerConfigurationPacketListenerImpl, ServerConfigurationNetworking.ConfigurationPacketHandler<ByteBufChannel.BytePayload>, ClientConfigurationNetworking.ConfigurationPayloadHandler<ByteBufChannel.BytePayload>> {

    public ConfigurationByteBufChannel(ResourceLocation identifier) {
        super(identifier);
    }

    public void registerCodec() {
        PayloadTypeRegistry.configurationS2C().register(id, codec);
        PayloadTypeRegistry.configurationC2S().register(id, codec);
    }

    public void registerServerHandler(ServerConfigurationNetworking.ConfigurationPacketHandler<BytePayload> handler) {
        ServerConfigurationNetworking.registerGlobalReceiver(id, handler);
    }

    public void registerClientHandler(ClientConfigurationNetworking.ConfigurationPayloadHandler<BytePayload> handler) {
        ClientConfigurationNetworking.registerGlobalReceiver(id, handler);
    }

    public void sendPacketC2S(FriendlyByteBuf byteBuf) {
        ClientConfigurationNetworking.send(new BytePayload() {
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

    public void sendPacketS2C(ServerConfigurationPacketListenerImpl player, FriendlyByteBuf byteBuf) {
        ServerConfigurationNetworking.send(player, new BytePayload() {
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
