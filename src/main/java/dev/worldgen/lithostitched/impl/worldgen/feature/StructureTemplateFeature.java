package dev.worldgen.lithostitched.impl.worldgen.feature;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.Optional;

public record StructureTemplateFeature(Identifier template, Holder<StructureProcessorList> processors, Optional<Rotation> rotation, LiquidSettings liquidSettings, Optional<Identifier> startJigsawName) implements Feature {
    public static final MapCodec<StructureTemplateFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Identifier.CODEC.fieldOf("template").forGetter(StructureTemplateFeature::template),
        StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(StructureTemplateFeature::processors),
        Rotation.CODEC.optionalFieldOf("rotation").forGetter(StructureTemplateFeature::rotation),
        LiquidSettings.CODEC.fieldOf("liquid_settings").orElse(LiquidSettings.APPLY_WATERLOGGING).forGetter(StructureTemplateFeature::liquidSettings),
        Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(StructureTemplateFeature::startJigsawName)
    ).apply(instance, StructureTemplateFeature::new));
    
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        StructureTemplateManager templateManager = level.getLevel().getServer().getStructureManager();
        StructureTemplate template = templateManager.getOrCreate(this.template());
        Rotation rotation = this.rotation().orElse(Rotation.getRandom(random));
        
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setLiquidSettings(this.liquidSettings()).setRandom(random);
        for (StructureProcessor processor : this.processors().value().list()) {
            settings.addProcessor(processor);
        }
        
        BlockPos jigsawPos = origin;
        if (this.startJigsawName().isPresent()) {
            Identifier startName = this.startJigsawName().get();
            ObjectArrayList<StructureTemplate.StructureBlockInfo> jigsawBlocks = template.filterBlocks(origin, new StructurePlaceSettings().setRotation(rotation.getRotated(Rotation.CLOCKWISE_180)), Blocks.JIGSAW, true);
            for (StructureTemplate.StructureBlockInfo jigsaw : jigsawBlocks) {
                Identifier jigsawName = Identifier.tryParse(jigsaw.nbt().getStringOr("name", ""));
                if (jigsawName == null || !jigsawName.equals(startName)) continue;
                jigsawPos = jigsaw.pos();
                break;
            }
        }
        
        Vec3i offset = jigsawPos.subtract(origin).multiply(-1);
        
        
        BlockPos placePos = origin.subtract(offset).offset(0, offset.getY() * 2, 0);
        
        template.placeInWorld(level, placePos, placePos, settings, random, 3);
        
        return true;
    }
    
    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
}
