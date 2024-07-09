package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.structure.condition.StructureCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;

public record DelegatingConfig(Holder<Structure> delegate, StructureCondition spawnCondition) {
    public static final MapCodec<DelegatingConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Structure.CODEC.fieldOf("delegate").forGetter(DelegatingConfig::delegate),
        StructureCondition.CODEC.fieldOf("spawn_condition").forGetter(DelegatingConfig::spawnCondition)
    ).apply(instance, DelegatingConfig::new));
}
