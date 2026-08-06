package dev.worldgen.lithostitched.duck;

import net.minecraft.core.RegistryAccess;

public interface RegistryHolder {
    RegistryAccess getRegistries();
    void setRegistries(RegistryAccess registries);
}
