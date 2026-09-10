package dev.o7moon.openboatutils;

import com.mojang.brigadier.arguments.*;
import dev.o7moon.openboatutils.network.ClientboundContextPacket;
import dev.o7moon.openboatutils.network.ClientboundSettingsPacket;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SingleplayerCommands {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> {
            dispatcher.register(
                    literal("stepsize").then(
                            argument("size", FloatArgumentType.floatArg()).executes(ctx ->
                            {
                                ServerPlayer player = ctx.getSource().getPlayer();
                                if (player == null) return 0;
                                FriendlyByteBuf packet = PacketByteBufs.create();
                                packet.writeShort(ClientboundSettingsPacket.SET_STEP_HEIGHT.ordinal());
                                packet.writeFloat(FloatArgumentType.getFloat(ctx, "size"));
                                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                                return 1;
                            })
                    )
            );

            dispatcher.register(
                    literal("reset").executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.RESET.ordinal());
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    })
            );

            dispatcher.register(
                    literal("defaultslipperiness").then(argument("slipperiness", FloatArgumentType.floatArg()).executes(ctx -> {
                                ServerPlayer player = ctx.getSource().getPlayer();
                                if (player == null) return 0;
                                FriendlyByteBuf packet = PacketByteBufs.create();
                                packet.writeShort(ClientboundSettingsPacket.SET_DEFAULT_SLIPPERINESS.ordinal());
                                packet.writeFloat(FloatArgumentType.getFloat(ctx, "slipperiness"));
                                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                                return 1;
                            })
                    )
            );

            dispatcher.register(
                    literal("blockslipperiness").then(argument("slipperiness", FloatArgumentType.floatArg()).then(argument("blocks", StringArgumentType.greedyString()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_BLOCKS_SLIPPERINESS.ordinal());
                        packet.writeFloat(FloatArgumentType.getFloat(ctx,"slipperiness"));
                        packet.writeUtf(StringArgumentType.getString(ctx,"blocks").trim());
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    })))
            );

            dispatcher.register(
                    literal("aircontrol").then(argument("enabled", BoolArgumentType.bool()).executes(ctx-> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_AIR_CONTROL.ordinal());
                        packet.writeBoolean(BoolArgumentType.getBool(ctx, "enabled"));
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("waterelevation").then(argument("enabled", BoolArgumentType.bool()).executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_BOAT_WATER_ELEVATION.ordinal());
                        packet.writeBoolean(BoolArgumentType.getBool(ctx, "enabled"));
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("falldamage").then(argument("enabled", BoolArgumentType.bool()).executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_BOAT_FALL_DAMAGE.ordinal());
                        packet.writeBoolean(BoolArgumentType.getBool(ctx, "enabled"));
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("jumpforce").then(argument("force", FloatArgumentType.floatArg()).executes(ctx -> {
                                ServerPlayer player = ctx.getSource().getPlayer();
                                if (player == null) return 0;
                                FriendlyByteBuf packet = PacketByteBufs.create();
                                packet.writeShort(ClientboundSettingsPacket.SET_BOAT_JUMP_FORCE.ordinal());
                                packet.writeFloat(FloatArgumentType.getFloat(ctx, "force"));
                                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                                return 1;
                            })
                    )
            );

            dispatcher.register(
                    literal("boatmode").then(argument("mode", StringArgumentType.string()).executes(ctx-> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        Modes mode;
                        try {
                            mode = Modes.valueOf(StringArgumentType.getString(ctx, "mode"));
                        } catch (Exception e) {
                            String valid_modes = "";
                            for (Modes m : Modes.values()) {
                                valid_modes += m.toString() + " ";
                            }
                            ctx.getSource().sendSystemMessage(Component.literal("Invalid mode! Valid modes are: "+valid_modes));
                            return 0;
                        }
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_MODE.ordinal());
                        packet.writeShort(mode.ordinal());
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("boatgravity").then(argument("gravity", DoubleArgumentType.doubleArg()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_GRAVITY.ordinal());
                        packet.writeDouble(DoubleArgumentType.getDouble(ctx, "gravity"));
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setyawaccel").then(argument("accel", FloatArgumentType.floatArg()).executes(ctx -> {
                                ServerPlayer player = ctx.getSource().getPlayer();
                                if (player == null) return 0;
                                FriendlyByteBuf packet = PacketByteBufs.create();
                                packet.writeShort(ClientboundSettingsPacket.SET_YAW_ACCEL.ordinal());
                                packet.writeFloat(FloatArgumentType.getFloat(ctx, "accel"));
                                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                                return 1;
                            })
                    )
            );

            dispatcher.register(
                    literal("setforwardaccel").then(argument("accel", FloatArgumentType.floatArg()).executes(ctx -> {
                                ServerPlayer player = ctx.getSource().getPlayer();
                                if (player == null) return 0;
                                FriendlyByteBuf packet = PacketByteBufs.create();
                                packet.writeShort(ClientboundSettingsPacket.SET_FORWARD_ACCEL.ordinal());
                                packet.writeFloat(FloatArgumentType.getFloat(ctx, "accel"));
                                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                                return 1;
                            })
                    )
            );

            dispatcher.register(
                    literal("setbackwardaccel").then(argument("accel", FloatArgumentType.floatArg()).executes(ctx -> {
                                ServerPlayer player = ctx.getSource().getPlayer();
                                if (player == null) return 0;
                                FriendlyByteBuf packet = PacketByteBufs.create();
                                packet.writeShort(ClientboundSettingsPacket.SET_BACKWARD_ACCEL.ordinal());
                                packet.writeFloat(FloatArgumentType.getFloat(ctx, "accel"));
                                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                                return 1;
                            })
                    )
            );

            dispatcher.register(
                    literal("setturnforwardaccel").then(argument("accel", FloatArgumentType.floatArg()).executes(ctx -> {
                                ServerPlayer player = ctx.getSource().getPlayer();
                                if (player == null) return 0;
                                FriendlyByteBuf packet = PacketByteBufs.create();
                                packet.writeShort(ClientboundSettingsPacket.SET_TURN_ACCEL.ordinal());
                                packet.writeFloat(FloatArgumentType.getFloat(ctx, "accel"));
                                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                                return 1;
                            })
                    )
            );

            dispatcher.register(
                    literal("allowaccelstacking").then(argument("allow", BoolArgumentType.bool()).executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.ALLOW_ACCEL_STACKING.ordinal());
                        packet.writeBoolean(BoolArgumentType.getBool(ctx, "allow"));
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(literal("sendversionpacket").executes(ctx->{
                ServerPlayer player = ctx.getSource().getPlayer();
                if (player == null) return 0;
                FriendlyByteBuf packet = PacketByteBufs.create();
                packet.writeShort(ClientboundSettingsPacket.RESEND_VERSION.ordinal());
                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                return 1;
            }));

            dispatcher.register(
                    literal("underwatercontrol").then(argument("enabled",BoolArgumentType.bool()).executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_UNDERWATER_CONTROL.ordinal());
                        packet.writeBoolean(BoolArgumentType.getBool(ctx, "enabled"));
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("surfacewatercontrol").then(argument("enabled", BoolArgumentType.bool()).executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_SURFACE_WATER_CONTROL.ordinal());
                        packet.writeBoolean(BoolArgumentType.getBool(ctx, "enabled"));
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("exclusiveboatmode").then(argument("mode",StringArgumentType.string()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        Modes mode;
                        try {
                            mode = Modes.valueOf(StringArgumentType.getString(ctx, "mode"));
                        } catch (Exception e) {
                            String valid_modes = "";
                            for (Modes m : Modes.values()) {
                                valid_modes += m.toString() + " ";
                            }
                            ctx.getSource().sendSystemMessage(Component.literal("Invalid mode! Valid modes are: "+valid_modes));
                            return 0;
                        }
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_EXCLUSIVE_MODE.ordinal());
                        packet.writeShort(mode.ordinal());
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("coyotetime").then(argument("ticks", IntegerArgumentType.integer()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        int time = IntegerArgumentType.getInteger(ctx,"ticks");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_COYOTE_TIME.ordinal());
                        packet.writeInt(time);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("waterjumping").then(argument("enabled", BoolArgumentType.bool()).executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_WATER_JUMPING.ordinal());
                        packet.writeBoolean(BoolArgumentType.getBool(ctx, "enabled"));
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("swimforce").then(argument("force", FloatArgumentType.floatArg()).executes(ctx -> {
                                ServerPlayer player = ctx.getSource().getPlayer();
                                if (player == null) return 0;
                                FriendlyByteBuf packet = PacketByteBufs.create();
                                packet.writeShort(ClientboundSettingsPacket.SET_SWIM_FORCE.ordinal());
                                packet.writeFloat(FloatArgumentType.getFloat(ctx, "force"));
                                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                                return 1;
                            })
                    )
            );

            dispatcher.register(
                    literal("removeblockslipperiness").then(argument("blocks", StringArgumentType.greedyString()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.REMOVE_BLOCKS_SLIPPERINESS.ordinal());
                        packet.writeUtf(StringArgumentType.getString(ctx,"blocks").trim());
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("clearslipperiness").executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.CLEAR_SLIPPERINESS.ordinal());
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    })
            );

            dispatcher.register(
                    literal("modeseries").then(argument("modes", StringArgumentType.greedyString()).executes(ctx-> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        Modes mode;
                        String[] strs = StringArgumentType.getString(ctx, "modes").split(",");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.MODE_SERIES.ordinal());
                        packet.writeShort(strs.length);
                        for (String modeStr : strs) {
                            try {
                                mode = Modes.valueOf(modeStr.trim());
                            } catch (Exception e) {
                                String valid_modes = "";
                                for (Modes m : Modes.values()) {
                                    valid_modes += m.toString() + " ";
                                }
                                ctx.getSource().sendSystemMessage(Component.literal("Invalid mode! Valid modes are: "+valid_modes));
                                return 0;
                            }
                            packet.writeShort(mode.ordinal());
                        }
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("exclusivemodeseries").then(argument("modes", StringArgumentType.greedyString()).executes(ctx-> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        Modes mode;
                        String[] strs = StringArgumentType.getString(ctx, "modes").split(",");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.EXCLUSIVE_MODE_SERIES.ordinal());
                        packet.writeShort(strs.length);
                        for (String modeStr : strs) {
                            try {
                                mode = Modes.valueOf(modeStr.trim());
                            } catch (Exception e) {
                                String valid_modes = "";
                                for (Modes m : Modes.values()) {
                                    valid_modes += m.toString() + " ";
                                }
                                ctx.getSource().sendSystemMessage(Component.literal("Invalid mode! Valid modes are: "+valid_modes));
                                return 0;
                            }
                            packet.writeShort(mode.ordinal());
                        }
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setblocksetting").then(argument("setting", StringArgumentType.string()).then(argument("value", FloatArgumentType.floatArg()).then(argument("blocks", StringArgumentType.greedyString()).executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        PerBlockSettingType setting;
                        try {
                            setting = PerBlockSettingType.valueOf(StringArgumentType.getString(ctx, "setting"));
                        } catch (Exception e) {
                            String valid_settings = "";
                            for (PerBlockSettingType s : PerBlockSettingType.values()) {
                                valid_settings += s.toString() + " ";
                            }
                            ctx.getSource().sendSystemMessage(Component.literal("Invalid setting! Valid settings are: "+valid_settings));
                            return 0;
                        }
                        float value = FloatArgumentType.getFloat(ctx, "value");
                        String blocks = StringArgumentType.getString(ctx, "blocks");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_PER_BLOCK.ordinal());
                        packet.writeShort(setting.ordinal());
                        packet.writeFloat(value);
                        packet.writeUtf(blocks);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))))
            );

            dispatcher.register(
                    literal("collisionmode").then(argument("ID", IntegerArgumentType.integer(0,CollisionMode.values().length - 1)).executes(ctx -> {
                                ServerPlayer player = ctx.getSource().getPlayer();
                                if (player == null) return 0;
                                FriendlyByteBuf packet = PacketByteBufs.create();
                                packet.writeShort(ClientboundSettingsPacket.SET_COLLISION_MODE.ordinal());
                                packet.writeShort(IntegerArgumentType.getInteger(ctx, "ID"));
                                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                                return 1;
                            })
                    )
            );

            dispatcher.register(
                    literal("stepwhilefalling").then(argument("enabled", BoolArgumentType.bool()).executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_STEP_WHILE_FALLING.ordinal());
                        packet.writeBoolean(BoolArgumentType.getBool(ctx, "enabled"));
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setinterpolationten").then(argument("enabled", BoolArgumentType.bool()).executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_INTERPOLATION_COMPAT.ordinal());
                        packet.writeBoolean(BoolArgumentType.getBool(ctx, "enabled"));
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setcollisionresolution").then(argument("resolution", IntegerArgumentType.integer(1, 50)).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        int time = IntegerArgumentType.getInteger(ctx,"resolution");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_COLLISION_RESOLUTION.ordinal());
                        packet.writeByte(time);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("clearcollisionfilter").executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.CLEAR_COLLISION_ENTITYTYPE_FILTER.ordinal());
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    })
            );

            dispatcher.register(
                    literal("addcollisionfilter").then(argument("entitytypes", StringArgumentType.greedyString()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        String entities = StringArgumentType.getString(ctx, "entitytypes").trim();
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.ADD_COLLISION_ENTITYTYPE_FILTER.ordinal());
                        packet.writeUtf(entities);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    })
            ));

            dispatcher.register(
                    literal("switchcontext").then(argument("context", StringArgumentType.greedyString()).executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;

                        String context = StringArgumentType.getString(ctx, "context").trim();

                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundContextPacket.SWITCH_CONTEXT.ordinal());
                        packet.writeUtf(context);
                        OpenBoatUtils.CONTEXT_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("dropcontext").then(argument("context", StringArgumentType.greedyString()).executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;

                        String context = StringArgumentType.getString(ctx, "context").trim();

                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundContextPacket.DROP_CONTEXT.ordinal());
                        packet.writeUtf(context);
                        OpenBoatUtils.CONTEXT_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setwalltapmultiplier").then(argument("multiplier", FloatArgumentType.floatArg()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        float aFloat = FloatArgumentType.getFloat(ctx,"multiplier");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_WALLTAP_MULTIPLIER.ordinal());
                        packet.writeFloat(aFloat);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setjumps").then(argument("jumps", IntegerArgumentType.integer()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        int aInt = IntegerArgumentType.getInteger(ctx,"jumps");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_JUMPS.ordinal());
                        packet.writeInt(aInt);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setscale").then(argument("scale", FloatArgumentType.floatArg()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        float aFloat = FloatArgumentType.getFloat(ctx,"scale");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_SCALE.ordinal());
                        packet.writeFloat(aFloat);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setstepupslipperiness").then(argument("slipperiness", FloatArgumentType.floatArg()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        float aFloat = FloatArgumentType.getFloat(ctx,"slipperiness");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_STEP_UP_SLIPPERINESS.ordinal());
                        packet.writeFloat(aFloat);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setresetonworldload").then(argument("enabled", BoolArgumentType.bool()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        boolean enabled = BoolArgumentType.getBool(ctx,"enabled");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_RESET_ON_WORLD_LOAD.ordinal());
                        packet.writeBoolean(enabled);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("fixdoublewaterelevation").then(argument("enabled", BoolArgumentType.bool()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        boolean enabled = BoolArgumentType.getBool(ctx,"enabled");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_FIX_DOUBLE_WATER_ELEVATION.ordinal());
                        packet.writeBoolean(enabled);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setlateralslipperiness").then(argument("slipperiness", FloatArgumentType.floatArg()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        float aFloat = FloatArgumentType.getFloat(ctx,"slipperiness");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_LATERAL_SLIPPERINESS.ordinal());
                        packet.writeFloat(aFloat);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setbrakeslipperiness").then(argument("brakeslipperiness", FloatArgumentType.floatArg()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        float aFloat = FloatArgumentType.getFloat(ctx,"brakeslipperiness");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_BRAKE_SLIPPERINESS.ordinal());
                        packet.writeFloat(aFloat);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("applyimpulse").then(argument("x", DoubleArgumentType.doubleArg())
                            .then(argument("y", DoubleArgumentType.doubleArg())
                                    .then(argument("z", DoubleArgumentType.doubleArg())
                                            .executes(ctx -> {
                                                ServerPlayer player = ctx.getSource().getPlayer();
                                                if (player == null) return 0;

                                                double x = DoubleArgumentType.getDouble(ctx, "x");
                                                double y = DoubleArgumentType.getDouble(ctx, "y");
                                                double z = DoubleArgumentType.getDouble(ctx, "z");

                                                FriendlyByteBuf packet = PacketByteBufs.create();
                                                packet.writeShort(ClientboundSettingsPacket.APPLY_IMPULSE.ordinal());
                                                packet.writeDouble(x);
                                                packet.writeDouble(y);
                                                packet.writeDouble(z);

                                                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                                                return 1;
                                            })
                                    )
                            ))
            );

            dispatcher.register(
                    literal("applyimpulserelative").then(argument("x", DoubleArgumentType.doubleArg())
                            .then(argument("y", DoubleArgumentType.doubleArg())
                                    .then(argument("z", DoubleArgumentType.doubleArg())
                                            .executes(ctx -> {
                                                ServerPlayer player = ctx.getSource().getPlayer();
                                                if (player == null) return 0;

                                                double x = DoubleArgumentType.getDouble(ctx, "x");
                                                double y = DoubleArgumentType.getDouble(ctx, "y");
                                                double z = DoubleArgumentType.getDouble(ctx, "z");

                                                FriendlyByteBuf packet = PacketByteBufs.create();
                                                packet.writeShort(ClientboundSettingsPacket.APPLY_IMPULSE_RELATIVE.ordinal());
                                                packet.writeDouble(x);
                                                packet.writeDouble(y);
                                                packet.writeDouble(z);

                                                OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                                                return 1;
                                            })
                                    )
                            ))
            );

            dispatcher.register(
                    literal("setmultistepping").then(argument("enabled", BoolArgumentType.bool()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        boolean enabled = BoolArgumentType.getBool(ctx,"enabled");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_MULTI_STEPPING.ordinal());
                        packet.writeBoolean(enabled);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setmaxspeed").then(argument("maxspeed", FloatArgumentType.floatArg()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        float aFloat = FloatArgumentType.getFloat(ctx,"maxspeed");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_MAX_SPEED.ordinal());
                        packet.writeFloat(aFloat);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("setmaxspeedresistance").then(argument("resistance", FloatArgumentType.floatArg()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        float aFloat = FloatArgumentType.getFloat(ctx,"resistance");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_MAX_SPEED_RESISTANCE.ordinal());
                        packet.writeFloat(aFloat);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );

            dispatcher.register(
                    literal("sethoneycompat").then(argument("enabled", BoolArgumentType.bool()).executes(ctx->{
                        ServerPlayer player = ctx.getSource().getPlayer();
                        if (player == null) return 0;
                        boolean enabled = BoolArgumentType.getBool(ctx,"enabled");
                        FriendlyByteBuf packet = PacketByteBufs.create();
                        packet.writeShort(ClientboundSettingsPacket.SET_HONEY_COMPATIBILITY.ordinal());
                        packet.writeBoolean(enabled);
                        OpenBoatUtils.SETTING_CHANNEL.sendPacketS2C(player, packet);
                        return 1;
                    }))
            );
        });
    }
}
