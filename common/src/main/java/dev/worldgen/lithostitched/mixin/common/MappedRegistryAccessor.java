package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MappedRegistry.class)
public interface MappedRegistryAccessor<T> {

    @Accessor("byValue")
    Map<T, Holder.Reference<T>> getByValue();
}