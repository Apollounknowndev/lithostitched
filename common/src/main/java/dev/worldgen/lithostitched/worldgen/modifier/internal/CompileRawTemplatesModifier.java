package dev.worldgen.lithostitched.worldgen.modifier.internal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.access.StructurePoolAccess;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.TrueModifierPredicate;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public record CompileRawTemplatesModifier() implements Modifier {

    public static final Codec<CompileRawTemplatesModifier> CODEC = Codec.unit(CompileRawTemplatesModifier::new);

    @Override
    public ModifierPredicate getPredicate() {
        return TrueModifierPredicate.INSTANCE;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.AFTER_ALL;
    }

    @Override
    public void applyModifier(RegistryAccess registries) {
        var poolRegistry = registries.registryOrThrow(Registries.TEMPLATE_POOL);
        for (StructureTemplatePool pool : poolRegistry) {
            ((StructurePoolAccess)pool).compileRawTemplates();
        }
    }

    @Override
    public void applyModifier() {

    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
