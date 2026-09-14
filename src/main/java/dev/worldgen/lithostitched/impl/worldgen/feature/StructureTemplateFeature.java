package dev.worldgen.lithostitched.impl.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.LithostitchedCodecs;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import java.util.Optional;

public record StructureTemplateFeature(WeightedList<Identifier> template, Holder<StructureProcessorList> processors, Optional<Rotation> rotation, LiquidSettings liquidSettings, boolean placeOnCenter) implements Feature {
    public static final MapCodec<StructureTemplateFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LithostitchedCodecs.compactWeightedList(Identifier.CODEC, false).fieldOf("template").forGetter(StructureTemplateFeature::template),
        StructureProcessorType.LIST_CODEC.fieldOf("processors").forGetter(StructureTemplateFeature::processors),
        Rotation.CODEC.optionalFieldOf("rotation").forGetter(StructureTemplateFeature::rotation),
        LiquidSettings.CODEC.fieldOf("liquid_settings").orElse(LiquidSettings.APPLY_WATERLOGGING).forGetter(StructureTemplateFeature::liquidSettings),
        Codec.BOOL.optionalFieldOf("place_on_center", true).forGetter(StructureTemplateFeature::placeOnCenter)
    ).apply(instance, StructureTemplateFeature::new));
    
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        StructureTemplateManager templateManager = level.getLevel().getServer().getStructureTemplateManager();
        StructureTemplate template = templateManager.getOrCreate(this.template().getRandomOrThrow(random));
        Rotation rotation = this.rotation().orElse(Rotation.getRandom(random));
        
        StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rotation).setLiquidSettings(this.liquidSettings()).setRandom(random);
        for (StructureProcessor processor : this.processors().value().list()) {
            settings.addProcessor(processor);
        }
        
        Vec3i offsetX = this.getRotatedOffset(rotation, Direction.Axis.X, template);
        Vec3i offsetZ = this.getRotatedOffset(rotation, Direction.Axis.Z, template);
        BlockPos pos = origin.offset(offsetX).offset(offsetZ);
        
        template.placeInWorld(level, pos, pos, settings, random, 3);
        
        return true;
    }
    
    private Vec3i getRotatedOffset(Rotation rotation, Direction.Axis axis, StructureTemplate template) {
        return rotation.rotate(axis.getNegative()).getUnitVec3i().multiply(template.getSize().get(axis) / 2);
    }
    
    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
}
