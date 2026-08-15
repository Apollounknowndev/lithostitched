package dev.worldgen.lithostitched.mixin.common.timeline;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.impl.duck.BiomeTimelineDuck;
import dev.worldgen.lithostitched.util.CodecExtender;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.timeline.Timeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(Biome.class)
public class BiomeMixin implements BiomeTimelineDuck {
	@Unique private HolderSet<Timeline> timelines = HolderSet.direct();
	
	@Override
	public void lithostitched$addTimeline(Holder<Timeline> timeline) {
		List<Holder<Timeline>> mergedTimelines = new ArrayList<>(this.timelines.stream().toList());
		mergedTimelines.add(timeline);
		this.timelines = HolderSet.direct(mergedTimelines);
	}
	
	@Override
	public void lithostitched$setTimelines(HolderSet<Timeline> timeline) {
		this.timelines = timeline;
	}
	
	@Override
	public HolderSet<Timeline> lithostitched$getTimelines() {
		return this.timelines;
	}
	
	@ModifyExpressionValue(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/codecs/RecordCodecBuilder;create(Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"
		)
	)
	private static Codec<Biome> addTimelineField(Codec<Biome> codec) {
		return CodecExtender.extend(
			codec,
			(instance, wrapper) -> instance.group(
				wrapper,
				RegistryCodecs.homogeneousList(Registries.TIMELINE).lenientOptionalFieldOf("lithostitched:timelines", HolderSet.direct()).forGetter(biome -> BiomeTimelineDuck.cast(biome).lithostitched$getTimelines())
			).apply(
				instance,
				(biome, timelines) -> {
					BiomeTimelineDuck.cast(biome).lithostitched$setTimelines(timelines);
					return biome;
				}
			)
		);
	}
}
