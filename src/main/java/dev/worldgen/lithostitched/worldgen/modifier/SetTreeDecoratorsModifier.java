package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.TreeConfigurationAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.registrySet;

public record SetTreeDecoratorsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<ConfiguredFeature<?, ?>> features, List<TreeDecorator> decorators, boolean append) implements WorldgenModifier {
    public static final MapCodec<SetTreeDecoratorsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(SetTreeDecoratorsModifier::priority),
        registrySet(Registries.CONFIGURED_FEATURE, "features").forGetter(SetTreeDecoratorsModifier::features),
        TreeDecorator.CODEC.listOf().fieldOf("decorators").forGetter(SetTreeDecoratorsModifier::decorators),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetTreeDecoratorsModifier::append)
    ).apply(instance, SetTreeDecoratorsModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        this.features.stream().forEach(this::applyModifier);
    }

    private void applyModifier(Holder<ConfiguredFeature<?,?>> feature) {
        if (feature.value().config() instanceof TreeConfiguration config) {
            List<TreeDecorator> mergedDecorators = new ArrayList<>();
            if (this.append) {
                mergedDecorators.addAll(config.decorators);
            }
            mergedDecorators.addAll(this.decorators);
            ((TreeConfigurationAccessor)config).lithostitched$setDecorators(mergedDecorators);
        }
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
