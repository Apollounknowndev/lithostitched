package dev.worldgen.lithostitched.registry;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.LithostitchedCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.SurfaceRules;

public final class LithostitchedMaterialRules {
    public static final ResourceKey<Codec<? extends SurfaceRules.RuleSource>> TRANSIENT_MERGED = LithostitchedCommon.createResourceKey(Registries.MATERIAL_RULE, "transient_merged");

}
