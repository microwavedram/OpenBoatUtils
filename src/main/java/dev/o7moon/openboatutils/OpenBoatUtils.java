package dev.o7moon.openboatutils;

import dev.o7moon.openboatutils.network.ConfigurationByteBufChannel;
import dev.o7moon.openboatutils.network.PlayByteBufChannel;
import dev.o7moon.openboatutils.network.ServerboundSettingsPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OpenBoatUtils extends MutableContext implements ModInitializer {

    public static final String NAMESPACE = "openboatutils";
    public static final int VERSION = 22;
    public static final boolean UNSTABLE = true;

    public static final Logger LOG = LoggerFactory.getLogger("OpenBoatUtils");

    public static final PlayByteBufChannel SETTING_CHANNEL = new PlayByteBufChannel(ResourceLocation.fromNamespaceAndPath(NAMESPACE, "settings"));
    public static final PlayByteBufChannel CONTEXT_CHANNEL = new PlayByteBufChannel(ResourceLocation.fromNamespaceAndPath(NAMESPACE, "context"));
    public static final ConfigurationByteBufChannel CONFIGURATION_CHANNEL = new ConfigurationByteBufChannel(ResourceLocation.fromNamespaceAndPath(NAMESPACE, "configuration"));

    public static final ResourceLocation DEFAULT_CONTEXT = ResourceLocation.fromNamespaceAndPath(NAMESPACE, "default");

    public static OpenBoatUtils instance;
    public static Minecraft minecraft;

    private final Map<ResourceLocation, StoredContext> stored_contexts = new HashMap<>();
    private final Map<UUID, EntityContext> entity_contexts = new HashMap<>();

    private boolean interpolationCompatibility = false;
    private boolean resetOnWorldLoad = true;

    private @Nullable ISettingContext activeContext = null;

    public OpenBoatUtils() {
        instance = this;
        minecraft = Minecraft.getInstance();
    }

    @Override
    public void onInitialize() {
        SETTING_CHANNEL.registerCodec();
        CONTEXT_CHANNEL.registerCodec();
        CONFIGURATION_CHANNEL.registerCodec();

        SETTING_CHANNEL.registerServerHandler((bytePayload, context) -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(bytePayload.getData()));

            context.server().execute(() -> {
                ServerboundSettingsPacket.handlePacket(buf);
                buf.release();
            });
        });

        SingleplayerCommands.registerCommands();
    }

    public void resetAll() {
        setActiveContext(this);

        interpolationCompatibility = false;
        resetOnWorldLoad = true;
    }

    public static void sendVersionPacket() {
        FriendlyByteBuf packet = PacketByteBufs.create();
        packet.writeShort(ServerboundSettingsPacket.VERSION.ordinal());
        packet.writeInt(VERSION);
        packet.writeBoolean(UNSTABLE);

        SETTING_CHANNEL.sendPacketC2S(packet);
    }

    @Override
    public void switchTo() {
        OpenBoatUtils.instance.applyFrom(ISettingContext.VANILLA);
    }

    public @NotNull ISettingContext getStoredContext(ResourceLocation identifier) {
        if (identifier.equals(DEFAULT_CONTEXT)) return this;

        return stored_contexts.computeIfAbsent(identifier, StoredContext::new);
    }

    public @Nullable EntityContext getEntityContext(UUID uuid) {
        return entity_contexts.get(uuid);
    }

    public void putEntityContext(UUID uuid, @NotNull EntityContext context) {
        entity_contexts.put(uuid, context);
    }

    public @Nullable StoredContext dropStoredContext(ResourceLocation identifier) {
        return stored_contexts.remove(identifier);
    }

    public void putStoredContext(ResourceLocation identifier, @NotNull StoredContext context) {
        stored_contexts.put(identifier, context);
    }

    public void setInterpolationCompatibility(boolean interpolationCompatibility) {
        this.interpolationCompatibility = interpolationCompatibility;
    }

    public boolean getInterpolationCompatibility() {
        return interpolationCompatibility;
    }

    public void setResetOnWorldLoad(boolean resetOnWorldLoad) {
        this.resetOnWorldLoad = resetOnWorldLoad;
    }

    public boolean getResetOnWorldLoad() { return resetOnWorldLoad; }

    public void setActiveContext(@Nullable ISettingContext context) {
        if (context == activeContext && context != this) return;

        if (context != null) context.switchTo();
        this.activeContext = context;
    }

    public void dropAllContextStores() {
        stored_contexts.clear();
        entity_contexts.clear();
    }

    public @Nullable ISettingContext getActiveContext() {
        @Nullable LocalPlayer player = Minecraft.getInstance().player;

        if (player != null && player.getVehicle() instanceof VehicleEntity vehicle) {
            @Nullable ISettingContext entityContext = getEntityContext(vehicle.getUUID());

            if (entityContext != null) {
                return entityContext;
            }
        }

        return activeContext;
    }
}
