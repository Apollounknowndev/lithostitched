package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Holder.Reference.class)
public interface HolderReferenceAccessor<T> {
    @Accessor("value")
    void setValue(T value);
}
