package dev.worldgen.lithostitched.api.worldgen.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.attribute.AmbientAdditionsSettings;
import net.minecraft.world.attribute.AmbientMoodSettings;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import org.joml.Vector3fc;

import java.util.Optional;

public record BiomeEffects(
    Optional<Integer> waterColor, Optional<Integer> foliageColor, Optional<Integer> dryFoliageColor,
    Optional<Integer> grassColor, Optional<BiomeSpecialEffects.GrassColorModifier> grassColorModifier
) {
    public static final Codec<BiomeEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.optionalFieldOf("water_color").forGetter(BiomeEffects::waterColor),
        Codec.INT.optionalFieldOf("foliage_color").forGetter(BiomeEffects::foliageColor),
        Codec.INT.optionalFieldOf("dry_foliage_color").forGetter(BiomeEffects::dryFoliageColor),
        Codec.INT.optionalFieldOf("grass_color").forGetter(BiomeEffects::grassColor),
        BiomeSpecialEffects.GrassColorModifier.CODEC.optionalFieldOf("grass_color_modifier").forGetter(BiomeEffects::grassColorModifier)
    ).apply(instance, BiomeEffects::new));
}