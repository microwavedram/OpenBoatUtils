package dev.o7moon.openboatutils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;

public enum Modes {
    BROKEN_SLIME_RALLY,//0
    BROKEN_SLIME_RALLY_BLUE,//1
    BROKEN_SLIME_BA_NOFD,//2
    BROKEN_SLIME_PARKOUR,//3
    BROKEN_SLIME_BA_BLUE_NOFD,//4
    BROKEN_SLIME_PARKOUR_BLUE,//5
    BROKEN_SLIME_BA,//6
    BROKEN_SLIME_BA_BLUE,//7
    RALLY,//8
    RALLY_BLUE,//9
    BA_NOFD,//10
    PARKOUR,//11
    BA_BLUE_NOFD,//12
    PARKOUR_BLUE,//13
    BA,//14
    BA_BLUE,//15
    JUMP_BLOCKS,//16
    BOOSTER_BLOCKS,//17
    DEFAULT_ICE,//18
    DEFAULT_NINE_EIGHT_FIVE,//19
    NOCOL_BOATS_AND_PLAYERS,//20
    NOCOL_ALL_ENTITIES,//21
    BA_JANKLESS,//22
    BA_BLUE_JANKLESS,//23
    DEFAULT_BLUE_ICE
    ;

    public static void setMode(Modes mode) {
        MutableContext context = OpenBoatUtils.instance;

        switch (mode) {
            case RALLY -> context
                    .setDefaultSlipperiness(0.98f)
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setStepSize(1.25f);

            case RALLY_BLUE -> context
                    .setDefaultSlipperiness(0.989f)
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setStepSize(1.25f);

            case BA_NOFD -> context
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), 0.98f)
                    .setStepSize(1.25f)
                    .setWaterElevation(true);

            case PARKOUR -> context
                    .setDefaultSlipperiness(0.98f)
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setJumpForce(0.36f)
                    .setStepSize(0.5f);

            case BA_BLUE_NOFD -> context
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), 0.989f)
                    .setStepSize(1.25f)
                    .setWaterElevation(true);

            case PARKOUR_BLUE -> context
                    .setDefaultSlipperiness(0.989f)
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setJumpForce(0.36f)
                    .setStepSize(0.5f);

            case BA -> context
                    .setAirControl(true)
                    .setBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), 0.98f)
                    .setStepSize(1.25f)
                    .setWaterElevation(true);

            case BA_BLUE -> context
                    .setAirControl(true)
                    .setBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), 0.989f)
                    .setStepSize(1.25f)
                    .setWaterElevation(true);

            case BROKEN_SLIME_RALLY -> context
                    .setDefaultSlipperiness(0.98f)
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setStepSize(1.25f)
                    .breakSlimePlease();

            case BROKEN_SLIME_RALLY_BLUE -> context
                    .setDefaultSlipperiness(0.989f)
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setStepSize(1.25f)
                    .breakSlimePlease();

            case BROKEN_SLIME_BA_NOFD -> context
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), 0.98f)
                    .setStepSize(1.25f)
                    .setWaterElevation(true)
                    .breakSlimePlease();

            case BROKEN_SLIME_PARKOUR -> context
                    .setDefaultSlipperiness(0.98f)
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setJumpForce(0.36f)
                    .setStepSize(0.5f)
                    .breakSlimePlease();

            case BROKEN_SLIME_BA_BLUE_NOFD -> context
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), 0.989f)
                    .setStepSize(1.25f)
                    .setWaterElevation(true)
                    .breakSlimePlease();

            case BROKEN_SLIME_PARKOUR_BLUE -> context
                    .setDefaultSlipperiness(0.989f)
                    .setFallDamage(false)
                    .setAirControl(true)
                    .setJumpForce(0.36f)
                    .setStepSize(0.5f)
                    .breakSlimePlease();

            case BROKEN_SLIME_BA -> context
                    .setAirControl(true)
                    .setBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), 0.98f)
                    .setStepSize(1.25f)
                    .setWaterElevation(true)
                    .breakSlimePlease();

            case BROKEN_SLIME_BA_BLUE -> context
                    .setAirControl(true)
                    .setBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), 0.989f)
                    .setStepSize(1.25f)
                    .setWaterElevation(true)
                    .breakSlimePlease();

            case JUMP_BLOCKS -> context
                    .setBlockSetting(BuiltInRegistries.BLOCK.getKey(Blocks.ORANGE_CONCRETE), PerBlockSettingType.JUMP_FORCE, 0.36f)
                    .setBlockSetting(BuiltInRegistries.BLOCK.getKey(Blocks.BLACK_CONCRETE), PerBlockSettingType.JUMP_FORCE, 0.0f)
                    .setBlockSetting(BuiltInRegistries.BLOCK.getKey(Blocks.GREEN_CONCRETE), PerBlockSettingType.JUMP_FORCE, 0.5f)
                    .setBlockSetting(BuiltInRegistries.BLOCK.getKey(Blocks.YELLOW_CONCRETE), PerBlockSettingType.JUMP_FORCE, 0.18f);

            case BOOSTER_BLOCKS -> context
                    .setBlockSetting(BuiltInRegistries.BLOCK.getKey(Blocks.MAGENTA_GLAZED_TERRACOTTA), PerBlockSettingType.FORWARDS_ACCEL, 0.08f)
                    .setBlockSetting(BuiltInRegistries.BLOCK.getKey(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA), PerBlockSettingType.YAW_ACCEL, 0.08f);

            case DEFAULT_ICE -> context.setDefaultSlipperiness(0.98f);

            case DEFAULT_NINE_EIGHT_FIVE -> context.setDefaultSlipperiness(0.985f);

            case NOCOL_BOATS_AND_PLAYERS -> context.setCollisionMode(CollisionMode.NO_BOATS_OR_PLAYERS);

            case NOCOL_ALL_ENTITIES -> context.setCollisionMode(CollisionMode.NO_ENTITIES);

            case BA_JANKLESS -> context
                    .setStepWhileFalling(true)
                    .setAirControl(true)
                    .setBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), 0.98f)
                    .setStepSize(1.25f)
                    .setWaterElevation(true);

            case BA_BLUE_JANKLESS -> context
                    .setStepWhileFalling(true)
                    .setAirControl(true)
                    .setBlockSlipperiness(BuiltInRegistries.BLOCK.getKey(Blocks.AIR), 0.989f)
                    .setStepSize(1.25f)
                    .setWaterElevation(true);

            case DEFAULT_BLUE_ICE -> context.setDefaultSlipperiness(0.989f);
        }
    }
}
