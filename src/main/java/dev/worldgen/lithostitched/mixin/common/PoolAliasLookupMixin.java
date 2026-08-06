package dev.worldgen.lithostitched.mixin.common;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PoolAliasLookup.class)
public interface PoolAliasLookupMixin {
    @Redirect(
        method = "create",
        at = @At(
            value = "INVOKE",
            remap = false,
            target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"
        )
    )
    private static ImmutableMap<?, ?> lithostitched$allowDuplicateEntries(ImmutableMap.Builder<?, ?> instance) {
        return instance.buildKeepingLast();
    }
}
