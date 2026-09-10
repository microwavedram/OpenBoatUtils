package dev.o7moon.openboatutils;

public enum PerBlockSettingType {
    JUMP_FORCE,
    FORWARDS_ACCEL,
    BACKWARDS_ACCEL,
    YAW_ACCEL,
    TURN_FORWARDS_ACCEL,
    WALLTAP_MULTIPLIER,
    JUMPS,
    COYOTE_TIME,
    STEP_UP_SLIPPERINESS,
    LATERAL_SLIPPERINESS,
    BRAKE_SLIPPERINESS,
    MAX_SPEED,
    MAX_SPEED_RESISTANCE;

    public float fromContext(ISettingContext context) {
        return switch (this) {
            case JUMP_FORCE -> context.getJumpForce();
            case FORWARDS_ACCEL -> context.getForwardAccel();
            case BACKWARDS_ACCEL -> context.getBackwardAccel();
            case YAW_ACCEL -> context.getYawAccel();
            case TURN_FORWARDS_ACCEL -> context.getTurnForwardAccel();
            case WALLTAP_MULTIPLIER -> context.getWalltapMultiplier();
            case JUMPS -> context.getJumps();
            case COYOTE_TIME -> context.getCoyoteTime();
            case STEP_UP_SLIPPERINESS -> context.getStepUpSlipperiness();
            case LATERAL_SLIPPERINESS -> context.getLateralSlipperiness();
            case BRAKE_SLIPPERINESS -> context.getBrakeSlipperiness();
            case MAX_SPEED -> context.getMaxSpeed();
            case MAX_SPEED_RESISTANCE -> context.getMaxSpeedResistance();
        };
    }
}