package dev.worldgen.lithostitched.impl.worldgen.modifier.internal;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.duck.StructurePoolAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;

public record CompileRawTemplatesModifier() implements WorldgenModifier {
    public static final MapCodec<CompileRawTemplatesModifier> CODEC = MapCodec.unit(CompileRawTemplatesModifier::new);
    
    @Override
    public Optional<LoadPredicate> predicate() {
        return Optional.empty();
    }
    
    @Override
    public void apply(RegistryAccess registries) {
        var poolRegistry = Lithostitched.registry(registries, Registries.TEMPLATE_POOL).stream().toList();
        for (StructureTemplatePool pool : poolRegistry) {
            ((StructurePoolAccess)pool).compileRawTemplates();
        }
    }
    
    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
