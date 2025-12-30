package dev.worldgen.lithostitched.worldgen.modifier.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.mixin.common.BiomeAccessor2;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.biome.Biome;

public record SetBiomeAttributesModifier(int priority, HolderSet<Biome> biomes, EnvironmentAttributeMap attributes, boolean append) implements Modifier {
    public static final MapCodec<SetBiomeAttributesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_DEFAULT.forGetter(SetBiomeAttributesModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(SetBiomeAttributesModifier::biomes),
        EnvironmentAttributeMap.CODEC_ONLY_POSITIONAL.fieldOf("attributes").forGetter(SetBiomeAttributesModifier::attributes),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetBiomeAttributesModifier::append)
    ).apply(instance, SetBiomeAttributesModifier::new));

    @Override
    public void applyModifier(RegistryAccess registries) {
        for (Holder<Biome> biome : this.biomes) {
            var builder = EnvironmentAttributeMap.builder();
            if (this.append) {
                builder.putAll(biome.value().getAttributes());
            }
            builder.putAll(this.attributes);

            ((BiomeAccessor2)(Object)biome.value()).setAttributes(builder.build());
            Modifier.resetRegistrationInfo(Lithostitched.registry(registries, Registries.BIOME), biome);
        }
    }

    @Override
    public void applyModifier() {

    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
