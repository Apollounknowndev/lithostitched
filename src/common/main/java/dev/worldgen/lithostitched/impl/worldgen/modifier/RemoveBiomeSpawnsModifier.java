package dev.worldgen.lithostitched.impl.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.LithostitchedPlatform;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.msrandom.multiplatform.annotations.Expect;

import java.util.Optional;

public record RemoveBiomeSpawnsModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, HolderSet<EntityType<?>> mobs) implements WorldgenModifier {
    public static final MapCodec<RemoveBiomeSpawnsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PREDICATE_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_REMOVE_CODEC.forGetter(RemoveBiomeSpawnsModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveBiomeSpawnsModifier::biomes),
        RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("mobs").forGetter(RemoveBiomeSpawnsModifier::mobs)
    ).apply(instance, RemoveBiomeSpawnsModifier::new));
    
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
