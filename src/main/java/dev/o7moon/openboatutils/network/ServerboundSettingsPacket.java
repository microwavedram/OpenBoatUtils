package dev.o7moon.openboatutils.network;

import dev.o7moon.openboatutils.OpenBoatUtils;
import io.netty.buffer.ByteBuf;

public enum ServerboundSettingsPacket {
    VERSION;

    public static void handlePacket(ByteBuf buf) {
        try {
            short packetID = buf.readShort();
            switch (packetID) {
                case 0:
                    int versionID = buf.readInt();
                    OpenBoatUtils.LOG.info("OpenBoatUtils version received by server: "+versionID);
            }
        } catch (Exception E) {
            OpenBoatUtils.LOG.error("Error when handling serverbound openboatutils packet: ");
            for (StackTraceElement e : E.getStackTrace()){
                OpenBoatUtils.LOG.error(e.toString());
            }
        }
    }
}
