package dev.worldgen.lithostitched.api.worldgen.poolelement;

import dev.worldgen.lithostitched.impl.worldgen.poolelement.DelegatingPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;

import java.util.Optional;
import java.util.function.UnaryOperator;

public interface LithostitchedPoolElements {
	static StructurePoolElement guaranteed(StructurePoolElement delegate, int forcedCount) {
		return new DelegatingPoolElement(delegate, Optional.empty(), Optional.of(forcedCount), Optional.empty());
	}
	
	static StructurePoolElement limited(StructurePoolElement delegate, int maxCount) {
		return new DelegatingPoolElement(delegate, Optional.empty(), Optional.empty(), Optional.of(maxCount));
	}
	
	static StructurePoolElement delegating(StructurePoolElement delegate, UnaryOperator<DelegatingElementBuilder> operator) {
		return new DelegatingPoolElement(operator.apply(DelegatingElementBuilder.create(delegate)).build());
	}
}
