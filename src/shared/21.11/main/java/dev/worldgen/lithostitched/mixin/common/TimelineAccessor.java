package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.timeline.AttributeTrack;
import net.minecraft.world.timeline.Timeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Timeline.class)
public interface TimelineAccessor {
    @Accessor("tracks")
    Map<EnvironmentAttribute<?>, AttributeTrack<?, ?>> getTracks();

    @Accessor("tracks")
    @Mutable
    void setTracks(Map<EnvironmentAttribute<?>, AttributeTrack<?, ?>> tracks);
}
