package dev.worldgen.lithostitched.worldgen.processor.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.registry.LithostitchedRegistryKeys;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

import java.util.function.Function;

public interface ProcessorCondition {
    @SuppressWarnings("unchecked")
    Codec<ProcessorCondition> BASE_CODEC = ExtraCodecs.lazyInitializedCodec(() -> {
        var registry = BuiltInRegistries.REGISTRY.get(LithostitchedRegistryKeys.PROCESSOR_CONDITION_TYPE.location());
        if (registry == null) throw new NullPointerException("Processor condition registry does not exist yet!");
        return ((Registry<MapCodec<? extends ProcessorCondition>>) registry).byNameCodec();
    }).dispatch(ProcessorCondition::codec, MapCodec::codec);

    Codec<ProcessorCondition> CODEC = LithostitchedCodecs.withAlternative(BASE_CODEC, BASE_CODEC.listOf(), AllOf::new);

    boolean test(WorldGenLevel level, Data data, StructurePlaceSettings settings, RandomSource random);

    MapCodec<? extends ProcessorCondition> codec();

    record Data(BlockPos pos, BlockPos pivot, StructureBlockInfo relative, StructureBlockInfo absolute) {}
}
