package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(TreeFeature.class)
public interface TreeFeatureAccessor {
	@Accessor("decorators")
	@Mutable
	void lithostitched$setDecorators(List<TreeDecorator> decorators);
}
