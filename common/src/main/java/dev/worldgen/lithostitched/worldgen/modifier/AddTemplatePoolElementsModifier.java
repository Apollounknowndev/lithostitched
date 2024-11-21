package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.access.StructurePoolAccess;
import dev.worldgen.lithostitched.mixin.common.StructureTemplatePoolAccessor;
import dev.worldgen.lithostitched.worldgen.structure.LithostitchedTemplates;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Modifier} implementation that adds template pool elements to a {@link StructureTemplatePool} entry.
 *
 * @author Apollo
 */
public record AddTemplatePoolElementsModifier(Holder<StructureTemplatePool> templatePool, List<Pair<StructurePoolElement, Integer>> elements) implements Modifier {
    public static final MapCodec<AddTemplatePoolElementsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StructureTemplatePool.CODEC.fieldOf("template_pool").forGetter(AddTemplatePoolElementsModifier::templatePool),
        Codec.mapPair(
            StructurePoolElement.CODEC.fieldOf("element"),
            Codec.intRange(1, 150).fieldOf("weight")
        ).codec().listOf().fieldOf("elements").forGetter(AddTemplatePoolElementsModifier::elements)
    ).apply(instance, AddTemplatePoolElementsModifier::new));

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.ADD;
    }

    @Override
    public void applyModifier() {
        StructureTemplatePoolAccessor poolAccessor = ((StructureTemplatePoolAccessor)this.templatePool().value());
        StructurePoolAccess lithostitchedPoolAccessor = ((StructurePoolAccess)this.templatePool().value());

        List<Pair<StructurePoolElement, Integer>> rawTemplates = new ArrayList<>(poolAccessor.getRawTemplates());
        rawTemplates.addAll(this.elements());
        poolAccessor.setRawTemplates(rawTemplates);

        ObjectArrayList<StructurePoolElement> vanillaTemplates = new ObjectArrayList<>(poolAccessor.getVanillaTemplates());
        LithostitchedTemplates lithostitchedTemplates = lithostitchedPoolAccessor.getLithostitchedTemplates();

        for (Pair<StructurePoolElement, Integer> pair : this.elements()) {
            lithostitchedTemplates.add(pair.getFirst(), pair.getSecond());
            for (int i = 0; i < pair.getSecond(); ++i) {
                vanillaTemplates.add(pair.getFirst());
            }
        }

        poolAccessor.setVanillaTemplates(vanillaTemplates);
    }
}
