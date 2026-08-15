package dev.worldgen.lithostitched.worldgen.modifier.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.impl.duck.BiomeTimelineDuck;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.timeline.Timeline;

import java.util.Optional;

public record SetBiomeTimelineModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<Biome> biomes, Holder<Timeline> timeline, boolean append) implements WorldgenModifier {
    public static final MapCodec<SetBiomeTimelineModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(SetBiomeTimelineModifier::priority),
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(SetBiomeTimelineModifier::biomes),
        Timeline.CODEC.fieldOf("timeline").forGetter(SetBiomeTimelineModifier::timeline),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetBiomeTimelineModifier::append)
    ).apply(instance, SetBiomeTimelineModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        for (Holder<Biome> biome : this.biomes) {
            if (this.append) {
                BiomeTimelineDuck.cast(biome.value()).lithostitched$addTimeline(this.timeline);
            } else {
                BiomeTimelineDuck.cast(biome.value()).lithostitched$setTimelines(HolderSet.direct(this.timeline));
            }
        }
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
