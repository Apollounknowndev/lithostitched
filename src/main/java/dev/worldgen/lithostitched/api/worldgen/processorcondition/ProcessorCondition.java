package dev.worldgen.lithostitched.api.worldgen.processorcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.registry.LithostitchedRegistries;
import dev.worldgen.lithostitched.impl.worldgen.processor.condition.AllOf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import java.util.function.Function;

public interface ProcessorCondition {
    @SuppressWarnings("unchecked")
    Codec<ProcessorCondition> BASE_CODEC = Codec.lazyInitialized(() -> {
        var registry = BuiltInRegistries.REGISTRY.getOptional(LithostitchedRegistries.PROCESSOR_CONDITION_TYPE.identifier());
        if (registry.isEmpty()) throw new NullPointerException("Processor condition registry does not exist yet!");
        return ((Registry<MapCodec<? extends ProcessorCondition>>) registry.get()).byNameCodec();
    }).dispatch(ProcessorCondition::codec, Function.identity());

    Codec<ProcessorCondition> CODEC = Codec.withAlternative(BASE_CODEC, BASE_CODEC.listOf(), AllOf::new);

    boolean test(WorldGenLevel level, Data data, StructurePlaceSettings settings, RandomSource random);

    MapCodec<? extends ProcessorCondition> codec();

    record Data(BlockPos pos, BlockPos pivot, StructureBlockInfo relative, StructureBlockInfo absolute) {}
}
