package dev.o7moon.openboatutils;

//? >= 1.21.11 {
/*import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
*///? } else {
import net.minecraft.world.entity.vehicle.AbstractBoat;
//? }
import org.spongepowered.asm.mixin.Unique;

public interface GetNearbySetting {
    @Unique
    float openboatutils$getAverageNearbySetting(ISettingContext context, AbstractBoat instance, PerBlockSettingType setting);
}
