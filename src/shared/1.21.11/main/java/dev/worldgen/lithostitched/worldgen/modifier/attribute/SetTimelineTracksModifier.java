package dev.worldgen.lithostitched.worldgen.modifier.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.mixin.common.TimelineAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Util;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.timeline.AttributeTrack;
import net.minecraft.world.timeline.Timeline;

import java.util.HashMap;
import java.util.Map;

public record SetTimelineTracksModifier(int priority, HolderSet<Timeline> timelines, Map<EnvironmentAttribute<?>, AttributeTrack<?, ?>> tracks, boolean append) implements Modifier {
    private static final Codec<Map<EnvironmentAttribute<?>, AttributeTrack<?, ?>>> TRACKS_CODEC = Codec.dispatchedMap(EnvironmentAttributes.CODEC, Util.memoize(AttributeTrack::createCodec));
    public static final MapCodec<SetTimelineTracksModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_DEFAULT.forGetter(SetTimelineTracksModifier::priority),
        RegistryCodecs.homogeneousList(Registries.TIMELINE).fieldOf("timelines").forGetter(SetTimelineTracksModifier::timelines),
        TRACKS_CODEC.fieldOf("tracks").forGetter(SetTimelineTracksModifier::tracks),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetTimelineTracksModifier::append)
    ).apply(instance, SetTimelineTracksModifier::new));

    @Override
    public void applyModifier(RegistryAccess registries) {
        for (Holder<Timeline> timeline : this.timelines) {
            TimelineAccessor accessor = ((TimelineAccessor)timeline.value());

            HashMap<EnvironmentAttribute<?>, AttributeTrack<?, ?>> map = new HashMap<>(accessor.getTracks());

            if (this.append) {
                map.putAll(accessor.getTracks());
            }
            map.putAll(this.tracks);

            accessor.setTracks(map);
            Modifier.resetRegistrationInfo(Lithostitched.registry(registries, Registries.TIMELINE), timeline);
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
