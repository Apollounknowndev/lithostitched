package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.Optional;

public record StructureTemplateConfig(Identifier template, Holder<StructureProcessorList> processors, Optional<Rotation> rotation, LiquidSettings liquidSettings, Optional<Identifier> startJigsawName) implements FeatureConfiguration {
    public static final Codec<StructureTemplateConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("template").forGetter(StructureTemplateConfig::template),
        StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(StructureTemplateConfig::processors),
        Rotation.CODEC.optionalFieldOf("rotation").forGetter(StructureTemplateConfig::rotation),
        LiquidSettings.CODEC.fieldOf("liquid_settings").orElse(LiquidSettings.APPLY_WATERLOGGING).forGetter(StructureTemplateConfig::liquidSettings),
        Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(StructureTemplateConfig::startJigsawName)
    ).apply(instance, StructureTemplateConfig::new));
}