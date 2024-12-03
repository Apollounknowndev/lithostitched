package dev.worldgen.lithostitched.worldgen.blockpredicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.ruletest.AlternatePropertiesPredicate;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.blockpredicates.StateTestingPredicate;

public final class BlockStatePredicate extends StateTestingPredicate {
    public static final Codec<BlockStatePredicate> CODEC = RecordCodecBuilder.create(instance -> stateTestingCodec(instance).and(
        AlternatePropertiesPredicate.CODEC.fieldOf("properties").forGetter(BlockStatePredicate::properties)
    ).apply(instance, BlockStatePredicate::new));

    public static final BlockPredicateType<BlockStatePredicate> TYPE = () -> CODEC;
    private final AlternatePropertiesPredicate properties;

    public BlockStatePredicate(Vec3i offset, AlternatePropertiesPredicate properties) {
        super(offset);
        this.properties = properties;
    }

    public AlternatePropertiesPredicate properties() {
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
