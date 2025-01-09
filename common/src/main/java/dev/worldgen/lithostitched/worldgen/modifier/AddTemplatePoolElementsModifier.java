package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.access.StructurePoolAccess;
import dev.worldgen.lithostitched.mixin.common.StructureTemplatePoolAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import dev.worldgen.lithostitched.worldgen.structure.LithostitchedTemplates;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.ArrayList;
import java.util.List;

import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.registrySet;

/**
 * A {@link Modifier} implementation that adds template pool elements to a {@link StructureTemplatePool} entry.
 *
 * @author Apollo
 */
public record AddTemplatePoolElementsModifier(ModifierPredicate predicate, HolderSet<StructureTemplatePool> templatePools, List<Pair<StructurePoolElement, Integer>> elements) implements Modifier {
    public static final Codec<AddTemplatePoolElementsModifier> CODEC = RecordCodecBuilder.create(instance -> Modifier.addModifierFields(instance).and(instance.group(
        registrySet(Registries.TEMPLATE_POOL, "template_pool").forGetter(AddTemplatePoolElementsModifier::templatePools),
        Codec.mapPair(
            StructurePoolElement.CODEC.fieldOf("element"),
            Codec.intRange(1, 150).fieldOf("weight")
        ).codec().listOf().fieldOf("elements").forGetter(AddTemplatePoolElementsModifier::elements)
    )).apply(instance, AddTemplatePoolElementsModifier::new));

    @Override
    public ModifierPredicate getPredicate() {
        return this.predicate;
    }

    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.ADD;
    }

    @Override
    public void applyModifier() {
        this.templatePools.stream().map(Holder::value).forEach(this::applyModifier);
    }

    private void applyModifier(StructureTemplatePool templatePool) {
        StructureTemplatePoolAccessor poolAccessor = (StructureTemplatePoolAccessor)templatePool;

        List<Pair<StructurePoolElement, Integer>> rawTemplates = new ArrayList<>(poolAccessor.getRawTemplates());
        rawTemplates.addAll(this.elements());
        poolAccessor.setRawTemplates(rawTemplates);

        ObjectArrayList<StructurePoolElement> vanillaTemplates = new ObjectArrayList<>(poolAccessor.getVanillaTemplates());
        for (Pair<StructurePoolElement, Integer> pair : this.elements()) {
            for (int i = 0; i < pair.getSecond(); ++i) {
                vanillaTemplates.add(pair.getFirst());
            }
        }

        poolAccessor.setVanillaTemplates(vanillaTemplates);
    }
}
