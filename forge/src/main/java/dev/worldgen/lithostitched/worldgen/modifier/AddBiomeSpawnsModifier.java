package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraftforge.common.world.ForgeBiomeModifiers;

import java.util.List;

/**
 * A {@link Modifier} implementation that adds mob spawn data to {@link Biome} entries.
 *
 * @author Apollo
 */
public class AddBiomeSpawnsModifier extends AbstractBiomeModifier {
    public static final Codec<AddBiomeSpawnsModifier> CODEC = RecordCodecBuilder.create(instance -> addModifierFields(instance).and(instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(AddBiomeSpawnsModifier::biomes),
        Codec.mapEither(
            SpawnerData.CODEC.listOf().fieldOf("spawners"),
            SpawnerData.CODEC.fieldOf("spawners")
        ).xmap(
            to -> to.map(
                list -> list,
                List::of
            ),
            Either::left
        ).forGetter(AddBiomeSpawnsModifier::biomeSpawns)
    )).apply(instance, AddBiomeSpawnsModifier::new));
    private final HolderSet<Biome> biomes;
    private final List<SpawnerData> biomeSpawns;
    protected AddBiomeSpawnsModifier(ModifierPredicate predicate, HolderSet<Biome> biomes, List<SpawnerData> biomeSpawns) {
        super(predicate, new ForgeBiomeModifiers.AddSpawnsBiomeModifier(biomes, biomeSpawns));
        this.biomes = biomes;
        this.biomeSpawns = biomeSpawns;
    }

    public HolderSet<Biome> biomes() {
        return this.biomes;
    }

    public List<SpawnerData> biomeSpawns() {
        return this.biomeSpawns;
    }


    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
