package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import dev.worldgen.lithostitched.mixin.common.MappedRegistryAccessor;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.Optional;

/**
 * A {@link Modifier} implementation that replaces the biome climate settings of {@link Biome} entries.
 *
 * @author Apollo
 */
public record ReplaceClimateModifier(HolderSet<Biome> biomes, Biome.ClimateSettings climateSettings) implements Modifier {
    public static final MapCodec<ReplaceClimateModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(ReplaceClimateModifier::biomes),
        Biome.ClimateSettings.CODEC.fieldOf("climate").forGetter(ReplaceClimateModifier::climateSettings)
    ).apply(instance, ReplaceClimateModifier::new));

    @SuppressWarnings("unchecked")
    @Override
    public void applyModifier(RegistryAccess registries) {
        List<Holder<Biome>> biomes = this.biomes().stream().toList();
        Registry<Biome> registry = Lithostitched.registry(registries, Registries.BIOME);
        for (Holder<Biome> entry : biomes.stream().toList()) {
            this.applyModifier(entry.value());

            if (entry.unwrapKey().isPresent()) {
                ResourceKey<Biome> key = entry.unwrapKey().get();
                Optional<RegistrationInfo> knownPackInfo = registry.registrationInfo(key);
                knownPackInfo.ifPresent(registrationInfo -> ((MappedRegistryAccessor<Biome>)registry).lithostitched$getRegistrationInfos().put(key, new RegistrationInfo(Optional.empty(), registrationInfo.lifecycle())));
            }
        }
    }

    @Override
    public void applyModifier() {}

    public void applyModifier(Biome biome) {
        ((BiomeAccessor) (Object) biome).setClimateSettings(this.climateSettings());
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.MODIFY;
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
