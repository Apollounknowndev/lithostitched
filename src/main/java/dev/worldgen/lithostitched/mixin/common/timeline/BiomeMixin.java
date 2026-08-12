package dev.worldgen.lithostitched.mixin.common.timeline;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.impl.duck.BiomeTimelineDuck;
import dev.worldgen.lithostitched.impl.util.CodecExtender;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.timeline.Timeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(Biome.class)
public abstract class BiomeMixin implements BiomeTimelineDuck {
	@Unique private Holder<Timeline> timeline;
	
	@Override
	public void lithostitched$setTimeline(Holder<Timeline> timeline) {
		this.timeline = timeline;
	}
	
	@Override
	public Holder<Timeline> lithostitched$getTimeline() {
		return this.timeline;
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
				Timeline.CODEC.lenientOptionalFieldOf("lithostitched:timeline").forGetter(biome -> Optional.ofNullable(BiomeTimelineDuck.cast(biome).lithostitched$getTimeline()))
			).apply(
				instance,
				(biome, timeline) -> {
					BiomeTimelineDuck.cast(biome).lithostitched$setTimeline(timeline.orElse(null));
					return biome;
				}
			)
		);
	}
}
