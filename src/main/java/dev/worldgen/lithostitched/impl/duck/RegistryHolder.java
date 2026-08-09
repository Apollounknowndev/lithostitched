package dev.worldgen.lithostitched.impl.duck;

import net.minecraft.core.RegistryAccess;

public interface RegistryHolder {
    RegistryAccess getRegistries();
    void setRegistries(RegistryAccess registries);
}
