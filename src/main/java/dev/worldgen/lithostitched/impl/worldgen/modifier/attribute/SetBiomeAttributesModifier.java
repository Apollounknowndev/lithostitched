package dev.worldgen.lithostitched.impl.worldgen.modifier.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public record SetBiomeAttributesModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, EnvironmentAttributeMap attributes, boolean append) implements WorldgenModifier {
    public static final MapCodec<SetBiomeAttributesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(SetBiomeAttributesModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(SetBiomeAttributesModifier::biomes),
        EnvironmentAttributeMap.CODEC_ONLY_POSITIONAL.fieldOf("attributes").forGetter(SetBiomeAttributesModifier::attributes),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetBiomeAttributesModifier::append)
    ).apply(instance, SetBiomeAttributesModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        for (Holder<Biome> biome : this.biomes) {
            var builder = EnvironmentAttributeMap.builder();
            if (this.append) {
                builder.putAll(biome.value().getAttributes());
            }
            builder.putAll(this.attributes);

            ((BiomeAccessor)(Object)biome.value()).setAttributes(builder.build());
            WorldgenModifier.resetRegistrationInfo(Lithostitched.registry(registries, Registries.BIOME), biome);
        }
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
