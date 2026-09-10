package dev.o7moon.openboatutils.network;

import dev.o7moon.openboatutils.*;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
//? >= 1.21.11 {
/*import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
*///? } else {
import net.minecraft.world.entity.vehicle.AbstractBoat;
//? }
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public enum ClientboundSettingsPacket {
    RESET,
    SET_STEP_HEIGHT,
    SET_DEFAULT_SLIPPERINESS,
    SET_BLOCKS_SLIPPERINESS,
    SET_BOAT_FALL_DAMAGE,
    SET_BOAT_WATER_ELEVATION,
    SET_AIR_CONTROL,
    SET_BOAT_JUMP_FORCE,
    SET_MODE,
    SET_GRAVITY,
    SET_YAW_ACCEL,
    SET_FORWARD_ACCEL,
    SET_BACKWARD_ACCEL,
    SET_TURN_ACCEL,
    ALLOW_ACCEL_STACKING,
    RESEND_VERSION,
    SET_UNDERWATER_CONTROL,
    SET_SURFACE_WATER_CONTROL,
    SET_EXCLUSIVE_MODE,
    SET_COYOTE_TIME,
    SET_WATER_JUMPING,
    SET_SWIM_FORCE,
    REMOVE_BLOCKS_SLIPPERINESS,
    CLEAR_SLIPPERINESS,
    MODE_SERIES,
    EXCLUSIVE_MODE_SERIES,
    SET_PER_BLOCK,
    SET_COLLISION_MODE,
    SET_STEP_WHILE_FALLING,
    SET_INTERPOLATION_COMPAT(false),
    SET_COLLISION_RESOLUTION,
    ADD_COLLISION_ENTITYTYPE_FILTER,
    CLEAR_COLLISION_ENTITYTYPE_FILTER,
    COMPOUND,
    SET_WALLTAP_MULTIPLIER,
    SET_JUMPS,
    SET_SCALE,
    SET_STEP_UP_SLIPPERINESS,
    SET_RESET_ON_WORLD_LOAD(false),
    SET_FIX_DOUBLE_WATER_ELEVATION,
    SET_LATERAL_SLIPPERINESS,
    SET_BRAKE_SLIPPERINESS,
    APPLY_IMPULSE(false),
    APPLY_IMPULSE_RELATIVE(false),
    SET_MULTI_STEPPING,
    SET_MAX_SPEED,
    SET_MAX_SPEED_RESISTANCE,
    SET_HONEY_COMPATIBILITY;

    private final boolean isContext;

    ClientboundSettingsPacket() {
        this(true);
    }
    ClientboundSettingsPacket(boolean isContext) {
        this.isContext = isContext;
    }

    public boolean isNonContext() {
        return !isContext;
    }

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

                OpenBoatUtils.SETTING_CHANNEL.sendPacketC2S(packet);

                return;
            }

            ClientboundSettingsPacket[] packets = ClientboundSettingsPacket.values();

            if (packetID >= packets.length) return;

            ClientboundSettingsPacket packet = packets[packetID];

            @Nullable ISettingContext mutable = OpenBoatUtils.instance.getActiveContext();

            if (mutable == null) {
                mutable = OpenBoatUtils.instance;

                OpenBoatUtils.instance.setActiveContext(mutable);
            }

            if (!(mutable instanceof final MutableContext context)) return;

            handleContextPacketPayload(context, buf, packet, false);
        } catch (Exception E) {
            OpenBoatUtils.LOG.error("Error when handling clientbound openboatutils packet: ");
            for (StackTraceElement e : E.getStackTrace()){
                OpenBoatUtils.LOG.error(e.toString());
            }
        }
    }

    public static void handleContextPacket(MutableContext context, FriendlyByteBuf buf, boolean isCompound) {
        short packetID = buf.readShort();

        ClientboundSettingsPacket[] packets = ClientboundSettingsPacket.values();

        if (packetID >= packets.length) return;

        ClientboundSettingsPacket packet = packets[packetID];

        handleContextPacketPayload(context, buf, packet, isCompound);
    }

    public static void handleContextPacketPayload(MutableContext context, FriendlyByteBuf buf, ClientboundSettingsPacket packet, boolean isCompound) {

        // All of these are non-context settings, they are handled seperately.
        // Almost certainly should be out of this channel but backwards compatibility!!
        if (packet.isNonContext()) {
            switch (packet) {
                case SET_INTERPOLATION_COMPAT -> OpenBoatUtils.instance.setInterpolationCompatibility(buf.readBoolean());
                case SET_RESET_ON_WORLD_LOAD -> OpenBoatUtils.instance.setResetOnWorldLoad(buf.readBoolean());
                case APPLY_IMPULSE -> {

                    double x = buf.readDouble();
                    double y = buf.readDouble();
                    double z = buf.readDouble();

                    if (OpenBoatUtils.minecraft.player != null && OpenBoatUtils.minecraft.player.getVehicle() instanceof AbstractBoat boat) {
                        boat.setDeltaMovement(boat.getDeltaMovement().add(x, y, z));
                    }
                }
                case APPLY_IMPULSE_RELATIVE -> {
                    double localX = buf.readDouble();
                    double localY = buf.readDouble();
                    double localZ = buf.readDouble();

                    if (OpenBoatUtils.minecraft.player != null && OpenBoatUtils.minecraft.player.getVehicle() instanceof AbstractBoat boat) {
                        double yaw = Math.toRadians(-boat.getYRot());

                        double worldX = localX * Math.cos(yaw) + localZ * Math.sin(yaw);
                        double worldZ = localX * Math.sin(yaw) - localZ * Math.cos(yaw);

                        boat.setDeltaMovement(
                                boat.getDeltaMovement().add(worldX, localY, -worldZ)
                        );
                    }
                }
            }

            return;
        }

        final MutableContext finalContext = context;
        switch (packet) {
            case RESET -> {
                context.applyFrom(ISettingContext.VANILLA);

                // Special full disable (null context) if we are in the default context,
                // this should be a catchall for potential any mistakes in the default values
                // or similar as the mixin(s) does pretty much nothing if the context is null
                if (context == OpenBoatUtils.instance && !isCompound) {
                    OpenBoatUtils.instance.setActiveContext(null);
                }
            }
            case SET_STEP_HEIGHT -> {
                float stepSize = buf.readFloat();
                context.setStepSize(stepSize);
            }
            case SET_DEFAULT_SLIPPERINESS -> {
                context.setDefaultSlipperiness(buf.readFloat());
            }
            case SET_BLOCKS_SLIPPERINESS -> {
                float slipperiness = buf.readFloat();

                Arrays.stream(buf.readUtf().split(","))
                        .map(ResourceLocation::parse)
                        .forEach(block -> finalContext.setBlockSlipperiness(block, slipperiness));
            }
            case SET_BOAT_FALL_DAMAGE -> {
                context.setFallDamage(buf.readBoolean());
            }
            case SET_BOAT_WATER_ELEVATION -> {
                context.setWaterElevation(buf.readBoolean());
            }
            case SET_AIR_CONTROL -> {
                context.setAirControl(buf.readBoolean());
            }
            case SET_BOAT_JUMP_FORCE -> {
                context.setJumpForce(buf.readFloat());
            }
            case SET_MODE -> {
                Modes.setMode(Modes.values()[buf.readShort()]);
            }
            case SET_GRAVITY -> {
                context.setGravityForce(buf.readDouble());
            }
            case SET_YAW_ACCEL -> {
                context.setYawAccel(buf.readFloat());
            }
            case SET_FORWARD_ACCEL -> {
                context.setForwardAccel(buf.readFloat());
            }
            case SET_BACKWARD_ACCEL -> {
                context.setBackwardAccel(buf.readFloat());
            }
            case SET_TURN_ACCEL -> {
                context.setTurnForwardAccel(buf.readFloat());
            }
            case ALLOW_ACCEL_STACKING -> {
                context.setAllowAccelStacking(buf.readBoolean());
            }
            case RESEND_VERSION -> {
                OpenBoatUtils.sendVersionPacket();
            }
            case SET_UNDERWATER_CONTROL -> {
                context.setUnderwaterControl(buf.readBoolean());
            }
            case SET_SURFACE_WATER_CONTROL -> {
                context.setSurfaceWaterControl(buf.readBoolean());
            }
            case SET_EXCLUSIVE_MODE -> {
                short mode = buf.readShort();
                context.applyFrom(ISettingContext.VANILLA);
                Modes.setMode(Modes.values()[mode]);
            }
            case SET_COYOTE_TIME -> {
                context.setCoyoteTime(buf.readInt());
            }
            case SET_WATER_JUMPING -> {
                context.setWaterJumping(buf.readBoolean());
            }
            case SET_SWIM_FORCE -> {
                context.setSwimForce(buf.readFloat());
            }
            case REMOVE_BLOCKS_SLIPPERINESS -> {
                Arrays.stream(buf.readUtf().split(","))
                        .map(ResourceLocation::parse)
                        .forEach(context::unsetBlockSlipperiness);
            }
            case CLEAR_SLIPPERINESS -> {
                context.clearSlipperinessMap();
            }
            case MODE_SERIES -> {
                short amount = buf.readShort();
                for (int i = 0; i < amount; i++) {
                    Modes.setMode(Modes.values()[buf.readShort()]);
                }
            }
            case EXCLUSIVE_MODE_SERIES -> {
                context.applyFrom(ISettingContext.VANILLA);
                short amount = buf.readShort();
                for (int i = 0; i < amount; i++) {
                    short mode = buf.readShort();
                    Modes.setMode(Modes.values()[mode]);
                }
            }
            case SET_PER_BLOCK -> {
                short index = buf.readShort();
                float value = buf.readFloat();

                PerBlockSettingType[] settingTypes = PerBlockSettingType.values();

                if (index >= settingTypes.length) return;
                PerBlockSettingType setting = settingTypes[index];

                Arrays.stream(buf.readUtf().split(","))
                        .map(ResourceLocation::parse)
                        .forEach(block -> finalContext.setBlockSetting(block, setting, value));
            }
            case SET_COLLISION_MODE -> {
                short collisionMode = buf.readShort();

                CollisionMode[] collisionModes = CollisionMode.values();

                if (collisionMode >= collisionModes.length) return;

                context.setCollisionMode(collisionModes[collisionMode]);
            }
            case SET_STEP_WHILE_FALLING -> {
                context.setStepWhileFalling(buf.readBoolean());
            }
            case SET_COLLISION_RESOLUTION -> {
                context.setCollisionResolution(buf.readByte());
            }
            case ADD_COLLISION_ENTITYTYPE_FILTER -> {
                Arrays.stream(buf.readUtf().split(","))
                        //? >= 26.2 {
                        /*.map(string -> {
                            return BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.tryParse(string));
                        })
                        *///? } else {
                        .map(EntityType::byString)
                        //? }
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(context::addToCollisionFilter);
            }
            case CLEAR_COLLISION_ENTITYTYPE_FILTER -> {
                context.clearCollisionFilter();
            }
            case COMPOUND -> {
                int size = buf.readInt();

                for (int i = 0; i < size; i++) {
                    handleContextPacket(context, buf, true);
                }
            }
            case SET_WALLTAP_MULTIPLIER -> {
                context.setWalltapMultiplier(buf.readFloat());
            }
            case SET_JUMPS -> {
                context.setJumps(buf.readInt());
            }
            case SET_SCALE -> {
                context.setScale(buf.readFloat());
            }
            case SET_STEP_UP_SLIPPERINESS -> {
                context.setStepUpSlipperiness(buf.readFloat());
            }
            case SET_FIX_DOUBLE_WATER_ELEVATION -> {
                context.setFixDoubleWaterElevation(buf.readBoolean());
            }
            case SET_LATERAL_SLIPPERINESS -> {
                context.setLateralSlipperiness(buf.readFloat());
            }
            case SET_BRAKE_SLIPPERINESS -> {
                context.setBrakeSlipperiness(buf.readFloat());
            }
            case SET_MULTI_STEPPING -> {
                context.setHasMultiStepping(buf.readBoolean());
            }
            case SET_MAX_SPEED -> {
                context.setMaxSpeed(buf.readFloat());
            }
            case SET_MAX_SPEED_RESISTANCE -> {
                context.setMaxSpeedResistance(buf.readFloat());
            }
            case SET_HONEY_COMPATIBILITY -> {
                context.setHasHoneyCompatibility(buf.readBoolean());
            }
        }
    }
}
