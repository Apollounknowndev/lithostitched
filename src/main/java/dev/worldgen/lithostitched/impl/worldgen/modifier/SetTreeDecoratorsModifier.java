package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.modifier.target.HolderTarget;
import dev.worldgen.lithostitched.mixin.common.TreeFeatureAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record SetTreeDecoratorsModifier(Optional<LoadPredicate> predicate, int priority, HolderTarget<Feature> features, List<TreeDecorator> decorators, boolean append) implements WorldgenModifier {
    public static final MapCodec<SetTreeDecoratorsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(SetTreeDecoratorsModifier::priority),
        HolderTarget.createCodec(Registries.FEATURE).fieldOf("features").forGetter(SetTreeDecoratorsModifier::features),
        TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter(SetTreeDecoratorsModifier::decorators),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetTreeDecoratorsModifier::append)
    ).apply(instance, SetTreeDecoratorsModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        for (Holder<Feature> feature : this.features.getHolders(registries)) {
            this.applyModifier(feature);
        }
    }

    private void applyModifier(Holder<Feature> feature) {
        if (feature.value() instanceof TreeFeature tree) {
            List<TreeDecorator> mergedDecorators = new ArrayList<>();
            if (this.append) {
                mergedDecorators.addAll(tree.decorators());
            }
            mergedDecorators.addAll(this.decorators);
            ((TreeFeatureAccessor)(Object)tree).lithostitched$setDecorators(mergedDecorators);
        }
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
