package dev.o7moon.openboatutils.client;

import dev.o7moon.openboatutils.OpenBoatUtils;
import dev.o7moon.openboatutils.network.ClientboundContextPacket;
import dev.o7moon.openboatutils.network.ClientboundSettingsPacket;
import dev.o7moon.openboatutils.network.ServerboundSettingsPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;

import static dev.o7moon.openboatutils.OpenBoatUtils.*;

public class OpenBoatUtilsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SETTING_CHANNEL.registerClientHandler((bytePayload, context) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(bytePayload.getData()));
            context.client().execute(() -> {
                ClientboundSettingsPacket.handlePacket(buf);
                buf.release();
            });
        });

        CONTEXT_CHANNEL.registerClientHandler((bytePayload, context) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(bytePayload.getData()));
            context.client().execute(() -> {
                ClientboundContextPacket.handlePacket(buf);
                buf.release();
            });
        });

        ClientConfigurationConnectionEvents.START.register((handler, client) -> {
            FriendlyByteBuf buf = PacketByteBufs.create();

            buf.writeShort(ServerboundSettingsPacket.VERSION.ordinal());
            buf.writeInt(VERSION);
            buf.writeBoolean(UNSTABLE);

            CONFIGURATION_CHANNEL.sendPacketC2S(buf);
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            OpenBoatUtils.instance.resetAll();
            OpenBoatUtils.sendVersionPacket();
        });
    }
}
