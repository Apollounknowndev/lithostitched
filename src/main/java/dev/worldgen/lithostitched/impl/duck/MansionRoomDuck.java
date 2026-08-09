package dev.worldgen.lithostitched.impl.duck;

import dev.worldgen.lithostitched.impl.worldgen.modifier.template.TemplateLists;
import net.minecraft.util.RandomSource;

public interface MansionRoomDuck extends RegistryHolder {
    int lithostitched$floorNumber();

    default String lithostitched$getRandom(String name, RandomSource random) {
        return TemplateLists.getRandom(this.getRegistries(), TemplateLists.mansion(this.lithostitched$floorNumber(), name), random).toString();
    }
}
