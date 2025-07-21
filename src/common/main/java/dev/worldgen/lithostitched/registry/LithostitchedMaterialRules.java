package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;

public final class LithostitchedMaterialRules {
    public static final ResourceKey<MapCodec<? extends SurfaceRules.RuleSource>> TRANSIENT_MERGED = Lithostitched.key(Registries.MATERIAL_RULE, "transient_merged");
}
