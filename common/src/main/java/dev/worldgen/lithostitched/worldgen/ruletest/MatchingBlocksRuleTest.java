package dev.worldgen.lithostitched.worldgen.ruletest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import org.jetbrains.annotations.NotNull;

public class MatchingBlocksRuleTest extends RuleTest {
    public static final Codec<MatchingBlocksRuleTest> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(MatchingBlocksRuleTest::blocks),
        AlternatePropertiesPredicate.CODEC.fieldOf("properties").forGetter(MatchingBlocksRuleTest::properties),
        Codec.floatRange(0.0f, 1.0f).fieldOf("chance").orElse(1.0f).forGetter(MatchingBlocksRuleTest::chance)
    ).apply(instance, MatchingBlocksRuleTest::new));
    public static final RuleTestType<MatchingBlocksRuleTest> TYPE = () -> CODEC;

    private final HolderSet<Block> blocks;
    private final AlternatePropertiesPredicate properties;
    private final float chance;

    public MatchingBlocksRuleTest(HolderSet<Block> blocks, AlternatePropertiesPredicate properties, float chance) {
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

    public AlternatePropertiesPredicate properties() {
        return this.properties;
    }


    public boolean test(BlockState state, @NotNull RandomSource random) {
        return (state.is(this.blocks) && this.properties.matches(state) && (this.chance == 1f || random.nextFloat() < this.chance));
    }

    protected @NotNull RuleTestType<?> getType() {
        return TYPE;
    }
}
