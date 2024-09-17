package dev.worldgen.lithostitched.worldgen.ruletest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MatchingBlocksRuleTest extends RuleTest {
    public static final MapCodec<MatchingBlocksRuleTest> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(MatchingBlocksRuleTest::blocks),
        StatePropertiesPredicate.CODEC.optionalFieldOf("properties").forGetter(MatchingBlocksRuleTest::properties),
        Codec.floatRange(0.0f, 1.0f).fieldOf("chance").orElse(1.0f).forGetter(MatchingBlocksRuleTest::chance)
    ).apply(instance, MatchingBlocksRuleTest::new));
    public static final RuleTestType<MatchingBlocksRuleTest> TYPE = () -> CODEC;

    private final HolderSet<Block> blocks;
    private final Optional<StatePropertiesPredicate> properties;
    private final float chance;

    public MatchingBlocksRuleTest(HolderSet<Block> blocks, Optional<StatePropertiesPredicate> properties, float chance) {
        this.blocks = blocks;
        this.properties = properties;
        this.chance = chance;
    }

    public HolderSet<Block> blocks() {
        return this.blocks;
    }

    public float chance() {
        return this.chance;
    }

    public Optional<StatePropertiesPredicate> properties() {
        return this.properties;
    }


    public boolean test(BlockState state, @NotNull RandomSource random) {
        return (
            state.is(this.blocks) &&
            this.properties.map(predicate -> predicate.matches(state)).orElse(true) &&
            (this.chance == 1f || random.nextFloat() < this.chance)
        );
    }

    protected @NotNull RuleTestType<?> getType() {
        return TYPE;
    }
}
