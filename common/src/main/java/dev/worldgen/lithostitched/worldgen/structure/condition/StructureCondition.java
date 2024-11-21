package dev.worldgen.lithostitched.worldgen.structure.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.function.Function;

/**
 * The interface used for structure conditions.
 * Structure conditions can be used with the `lithostitched:delegating` structure type and provide extra fields to configure structure placement.
 *
 * @author Apollo
 */
public interface StructureCondition {
    @SuppressWarnings("unchecked")
    Codec<StructureCondition> BASE_CODEC = Codec.lazyInitialized(() -> {
        var registry = BuiltInRegistries.REGISTRY.get(LithostitchedRegistryKeys.STRUCTURE_CONDITION_TYPE.location());
        if (registry == null) throw new NullPointerException("Structure condition registry does not exist yet!");
        return ((Registry<MapCodec<? extends StructureCondition>>) registry).byNameCodec();
    }).dispatch(StructureCondition::codec, Function.identity());

    Codec<StructureCondition> CODEC = Codec.withAlternative(BASE_CODEC, BASE_CODEC.listOf(), AllOfStructureCondition::new);

    boolean test(Structure.GenerationContext context, BlockPos pos);

    MapCodec<? extends StructureCondition> codec();
}
