package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;

public record OreConfig(int size, List<Target> targets) implements FeatureConfiguration {
    public static final Codec<OreConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.intRange(0, 128).fieldOf("size").forGetter(OreConfig::size),
        Target.CODEC.listOf().fieldOf("targets").forGetter(OreConfig::targets)
    ).apply(instance, OreConfig::new));

    public record Target(BlockPredicate predicate, BlockStateProvider stateProvider) {
        public static final Codec<Target> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPredicate.CODEC.fieldOf("predicate").forGetter(Target::predicate),
            BlockStateProvider.CODEC.fieldOf("state_provider").forGetter(Target::stateProvider)
        ).apply(instance, Target::new));
    }
}
