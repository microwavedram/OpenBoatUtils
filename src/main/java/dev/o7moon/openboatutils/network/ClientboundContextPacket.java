package dev.o7moon.openboatutils.network;

import dev.o7moon.openboatutils.EntityContext;
import dev.o7moon.openboatutils.ISettingContext;
import dev.o7moon.openboatutils.OpenBoatUtils;
import dev.o7moon.openboatutils.StoredContext;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public enum ClientboundContextPacket {
    RESET_CONTEXT,
    SWITCH_CONTEXT,
    DROP_CONTEXT,
    STORE_CONTEXT,
    ENTITY_CONTEXT;

    public static void handlePacket(FriendlyByteBuf buf) {
        try {
            short packetID = buf.readShort();

            // Transaction Wrapper
            if (packetID == Short.MAX_VALUE) {
                int transactionId = buf.readInt();

                handlePacket(buf);

                FriendlyByteBuf packet = PacketByteBufs.create();
                packet.writeShort(Short.MAX_VALUE);
                packet.writeInt(transactionId);

                OpenBoatUtils.CONTEXT_CHANNEL.sendPacketC2S(packet);

                return;
            }

            ClientboundContextPacket[] packets = ClientboundContextPacket.values();

            if (packetID >= packets.length) return;

            ClientboundContextPacket packet = packets[packetID];

            handlePacket(buf, packet);
        } catch (Exception E) {
            OpenBoatUtils.LOG.error("Error when handling clientbound openboatutils packet: ");
            for (StackTraceElement e : E.getStackTrace()){
                OpenBoatUtils.LOG.error(e.toString());
            }
        }
    }

    public static void handlePacket(FriendlyByteBuf buf, ClientboundContextPacket packet) {
        switch (packet) {
            case RESET_CONTEXT -> {
                OpenBoatUtils.instance.setActiveContext(null);
            }
            case SWITCH_CONTEXT -> {
                ResourceLocation identifier = ResourceLocation.parse(buf.readUtf());

                if (identifier.getNamespace().equals(OpenBoatUtils.NAMESPACE)) {
                    if (!identifier.equals(OpenBoatUtils.DEFAULT_CONTEXT)) {
                        return;
                    }
                }

                ISettingContext context = OpenBoatUtils.instance.getStoredContext(identifier);

                OpenBoatUtils.instance.setActiveContext(context);
            }
            case DROP_CONTEXT -> {
                ResourceLocation identifier = ResourceLocation.parse(buf.readUtf());

                @Nullable ISettingContext context = OpenBoatUtils.instance.dropStoredContext(identifier);

                if (context != null && context == OpenBoatUtils.instance.getActiveContext()) {
                    OpenBoatUtils.instance.setActiveContext(null);
                }
            }
            case STORE_CONTEXT -> {
                ResourceLocation identifier = ResourceLocation.parse(buf.readUtf());

                StoredContext storedContext = new StoredContext(identifier);

                ClientboundSettingsPacket.handleContextPacketPayload(storedContext, buf, ClientboundSettingsPacket.COMPOUND, true);

                OpenBoatUtils.instance.putStoredContext(identifier, storedContext);
            }
            case ENTITY_CONTEXT -> {
                UUID id = UUID.fromString(buf.readUtf());

                EntityContext entityContext = new EntityContext(id);

                ClientboundSettingsPacket.handleContextPacketPayload(entityContext, buf, ClientboundSettingsPacket.COMPOUND, true);

                OpenBoatUtils.instance.putEntityContext(id, entityContext);
            }
        }
    }
}
