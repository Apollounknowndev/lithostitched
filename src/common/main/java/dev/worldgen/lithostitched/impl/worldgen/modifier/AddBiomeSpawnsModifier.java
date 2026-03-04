package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.api.worldgen.util.WeightedSpawnerData;
import dev.worldgen.lithostitched.impl.LithostitchedPlatform;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.msrandom.multiplatform.annotations.Expect;

import java.util.List;
import java.util.Optional;

public record AddBiomeSpawnsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, List<WeightedSpawnerData> biomeSpawns) implements WorldgenModifier {
    public static final MapCodec<AddBiomeSpawnsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PREDICATE_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(AddBiomeSpawnsModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddBiomeSpawnsModifier::biomes),
        Codec.mapEither(
            WeightedSpawnerData.CODEC.listOf().fieldOf("spawners"),
            WeightedSpawnerData.CODEC.fieldOf("spawners")
        ).xmap(
            either -> either.map(
                list -> list,
                List::of
            ),
            Either::left
        ).forGetter(AddBiomeSpawnsModifier::biomeSpawns)
    ).apply(instance, AddBiomeSpawnsModifier::new));
    
    @Override
    public void apply(RegistryAccess registries) {
        if (!LithostitchedPlatform.isFabric()) return;
        
        for (Holder<Biome> entry : this.biomes()) {
            this.applyModifier(entry.value());
        }
    }

    @Expect
    public void applyModifier(Biome biome);

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
    

}

