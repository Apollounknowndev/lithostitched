package dev.worldgen.lithostitched.mixin.common.mnbs;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.impl.duck.mnbs.MNBSDuck;
import dev.worldgen.lithostitched.impl.duck.mnbs.MNBSPLDuck;
import dev.worldgen.lithostitched.impl.util.CodecExtender;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MNBSMixin implements MNBSDuck {
	@Shadow @Mutable @Final
    private Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> parameters;

	@Override
	public Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> lithostitched$getEntries() {
		return this.parameters;
	}

	@Override
	public void lithostitched$setEntries(Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>> entries) {
		this.parameters = entries;
	}

	@Redirect(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/MapCodec;xmap(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/MapCodec;"
		)
	)
	private static MapCodec<MultiNoiseBiomeSource> wrapCodec(MapCodec<Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>>> original, final Function<? super Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>>, ? extends MultiNoiseBiomeSource> to, final Function<? super MultiNoiseBiomeSource, ? extends Either<Climate.ParameterList<Holder<Biome>>, Holder<MultiNoiseBiomeSourceParameterList>>> from) {
		return CodecExtender.extend(
			original.xmap(to, from),
			(instance, wrapper) -> instance.group(
				wrapper,
				RegistryOps.retrieveGetter(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST)
			).apply(
				instance,
				(mnbs, lookup) -> {
					MNBSDuck duck = ((MNBSDuck)mnbs);
					var biomeEntries = duck.lithostitched$getEntries();

					if (biomeEntries.left().isPresent()) {
						var rawEntries = biomeEntries.left().get().values();

						var overworldPreset = lookup.get(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
						if (overworldPreset.isEmpty() || !overworldPreset.get().isBound()) return mnbs;

						var migrationBiome = ((MNBSPLDuck)overworldPreset.get().value()).lithostitched$getMigrationBiome();
						if (migrationBiome.isPresent() && rawEntries.getLast().getSecond().is(migrationBiome.get())) {
							duck.lithostitched$setEntries(Either.right(overworldPreset.get()));
						}
					}
					return mnbs;
				}
			)
		);
	}
}