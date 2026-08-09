package dev.worldgen.lithostitched.impl.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public record CompositeFeature(HolderSet<PlacedFeature> features, Type placementType) implements Feature {
    public static final MapCodec<CompositeFeature> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(CompositeFeature::features),
        Type.CODEC.fieldOf("placement_type").orElse(Type.NEVER_CANCEL).forGetter(CompositeFeature::placementType)
    ).apply(i, CompositeFeature::new));
    
    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator generator, RandomSource random, BlockPos origin) {
        boolean anyPlaced = false;
        
        for (Holder<PlacedFeature> feature : this.features()) {
            boolean placed = feature.value().place(level, generator, random, origin);
            
            if (placed) {
                anyPlaced = true;
            }
            
            if (!this.placementType().shouldContinue(placed)) {
                break;
            }
        }
        
        return anyPlaced;
    }
    
    @Override
    public MapCodec<? extends Feature> codec() {
        return CODEC;
    }
    
    public enum Type implements StringRepresentable {
        NEVER_CANCEL("never_cancel", success -> true),
        CANCEL_ON_FAILURE("cancel_on_failure", success -> success),
        CANCEL_ON_SUCCESS("cancel_on_success", success -> !success),;
        
        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
        
        private final String name;
        private final Predicate<Boolean> continueCondition;
        
        Type(String name, Predicate<Boolean> continueCondition) {
            this.name = name;
            this.continueCondition = continueCondition;
        }
        
        @Override
        @NotNull
        public String getSerializedName() {
            return this.name;
        }
        
        public boolean shouldContinue(boolean success) {
            return continueCondition.test(success);
        }
    }
}
