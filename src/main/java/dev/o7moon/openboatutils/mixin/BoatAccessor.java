package dev.o7moon.openboatutils.mixin;

//? >= 1.21.11 {
/*import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
*///? } else {
import net.minecraft.world.entity.vehicle.AbstractBoat;
//? }
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractBoat.class)
public interface BoatAccessor {
    @Accessor("inputDown")
    boolean getPressingBack();
}
