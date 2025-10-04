package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.neoforged.neoforge.common.world.BiomeModifiers;

import java.util.List;

/**
 * A {@link Modifier} implementation that adds mob spawn data to {@link Biome} entries.
 *
 * @author Apollo
 */
public class AddBiomeSpawnsModifier extends AbstractBiomeModifier {
    private static final Codec<Weighted<SpawnerData>> SPAWNER_CODEC = Weighted.codec(SpawnerData.CODEC);
    public static final MapCodec<AddBiomeSpawnsModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddBiomeSpawnsModifier::biomes),
        Codec.mapEither(
            SPAWNER_CODEC.listOf().fieldOf("spawners"),
            SPAWNER_CODEC.fieldOf("spawners")
        ).xmap(
            to -> to.map(
                list -> list,
                List::of
            ),
            Either::left
        ).forGetter(AddBiomeSpawnsModifier::biomeSpawns)
    ).apply(instance, AddBiomeSpawnsModifier::new));
    private final HolderSet<Biome> biomes;
    private final List<Weighted<SpawnerData>> biomeSpawns;

    protected AddBiomeSpawnsModifier(HolderSet<Biome> biomes, List<Weighted<SpawnerData>> biomeSpawns) {
        super(new BiomeModifiers.AddSpawnsBiomeModifier(biomes, WeightedList.of(biomeSpawns)));
        this.biomes = biomes;
        this.biomeSpawns = biomeSpawns;
    }

    public HolderSet<Biome> biomes() {
        return this.biomes;
    }

    public List<Weighted<SpawnerData>> biomeSpawns() {
        return this.biomeSpawns;
    }


    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
