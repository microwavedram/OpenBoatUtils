package dev.o7moon.openboatutils.network;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public abstract class ByteBufChannel<T, S, C> {

    protected final CustomPacketPayload.Type<BytePayload> id;

    public final StreamCodec<FriendlyByteBuf, BytePayload> codec;

    public interface BytePayload extends CustomPacketPayload {
        ByteBuf getData();
    }

    public ByteBufChannel(ResourceLocation identifier) {
        this.id = new CustomPacketPayload.Type<>(identifier);

        codec = CustomPacketPayload.codec(
                this::encode,
                this::decode
        );
    }

    public abstract void registerCodec();

    public abstract void registerServerHandler(S handler);

    public abstract void registerClientHandler(C handler);

    public abstract void sendPacketC2S(FriendlyByteBuf byteBuf);

    public abstract void sendPacketS2C(T player, FriendlyByteBuf byteBuf);

    private void encode(BytePayload bytePayload, FriendlyByteBuf packetByteBuf) {
        packetByteBuf.writeBytes(bytePayload.getData());
    }

    private BytePayload decode(FriendlyByteBuf buf) {
        ByteBuf copy = buf.copy();

        buf.readerIndex(buf.writerIndex()); // so mc doesn't complain we haven't read all the bytes

        return new BytePayload() {
            @Override public ByteBuf getData() { return copy; }
            @Override public Type<? extends CustomPacketPayload> type() { return id; }
        };
    }
}
