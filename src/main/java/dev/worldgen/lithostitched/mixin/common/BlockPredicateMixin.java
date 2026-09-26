package dev.worldgen.lithostitched.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.impl.worldgen.blockpredicate.HolderHolderPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockPredicate.class)
public interface BlockPredicateMixin {
	@ModifyExpressionValue(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Lcom/mojang/serialization/Codec;dispatch(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/serialization/Codec;"
		)
	)
	private static Codec<BlockPredicate> addBlockPredicateHolderReferences(Codec<BlockPredicate> directCodec) {
		return RegistryCodecs.holder(LithostitchedRegistries.BLOCK_PREDICATE, directCodec).xmap(holder -> switch (holder) {
			case Holder.Direct<BlockPredicate> direct -> direct.value();
			case Holder.Reference<BlockPredicate> reference -> new HolderHolderPredicate(reference);
			default -> throw new IllegalStateException("Unexpected value: " + holder);
		}, value -> switch (value) {
			case HolderHolderPredicate(Holder<BlockPredicate> holder) -> holder;
			default -> Holder.direct(value);
		});
	}
}
