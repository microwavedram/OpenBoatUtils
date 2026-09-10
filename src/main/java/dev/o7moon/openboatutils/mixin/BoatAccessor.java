package dev.o7moon.openboatutils.mixin;

import net.minecraft.world.entity.vehicle.AbstractBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBoat.class)
public interface BoatAccessor {
    @Accessor("inputDown")
    boolean getPressingBack();
}
