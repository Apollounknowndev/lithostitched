package dev.worldgen.lithostitched.worldgen.blockpredicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;

public final class BlockStatePredicate extends StateTestingPredicate {
    public static final MapCodec<BlockStatePredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> stateTestingCodec(instance).and(
        StatePropertiesPredicate.CODEC.fieldOf("properties").forGetter(BlockStatePredicate::properties)
    ).apply(instance, BlockStatePredicate::new));

    public static final BlockPredicateType<BlockStatePredicate> TYPE = () -> CODEC;
    private final StatePropertiesPredicate properties;

    public BlockStatePredicate(Vec3i offset, StatePropertiesPredicate properties) {
        super(offset);
        this.properties = properties;
    }

    public StatePropertiesPredicate properties() {
        return properties;
    }

    @Override
    public boolean test(BlockState state) {
        return this.properties.matches(state);
    }

    @Override
    public BlockPredicateType<?> type() {
        return TYPE;
    }
}
