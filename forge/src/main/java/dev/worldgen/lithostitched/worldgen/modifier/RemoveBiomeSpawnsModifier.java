package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.modifier.predicate.ModifierPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.ForgeBiomeModifiers;

/**
 * A {@link Modifier} implementation that removes mob spawns from {@link Biome} entries.
 *
 * @author Apollo
 */
public class RemoveBiomeSpawnsModifier extends AbstractBiomeModifier {
    public static final Codec<RemoveBiomeSpawnsModifier> CODEC = RecordCodecBuilder.create(instance -> addModifierFields(instance).and(instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(RemoveBiomeSpawnsModifier::biomes),
        RegistryCodecs.homogeneousList(Registries.ENTITY_TYPE).fieldOf("mobs").forGetter(RemoveBiomeSpawnsModifier::mobs)
    )).apply(instance, RemoveBiomeSpawnsModifier::new));
    private final HolderSet<Biome> biomes;
    private final HolderSet<EntityType<?>> mobs;
    protected RemoveBiomeSpawnsModifier(ModifierPredicate predicate, HolderSet<Biome> biomes, HolderSet<EntityType<?>> mobs) {
        super(predicate, new ForgeBiomeModifiers.RemoveSpawnsBiomeModifier(biomes, mobs));
        this.biomes = biomes;
        this.mobs = mobs;
    }

    public HolderSet<Biome> biomes() {
        return this.biomes;
    }

    public HolderSet<EntityType<?>> mobs() {
        return this.mobs;
    }


    @Override
    public Codec<? extends Modifier> codec() {
        return CODEC;
    }
}
