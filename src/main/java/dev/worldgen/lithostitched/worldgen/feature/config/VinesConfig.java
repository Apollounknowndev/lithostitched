package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.api.util.WeightedList;
import net.minecraft.util.valueproviders.IntProviders;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Optional;

public record VinesConfig(WeightedList<Block> blocks, Optional<HolderSet<Block>> canPlaceOn, IntProvider maxLength) implements FeatureConfiguration {
    private static final WeightedList<Block> DEFAULT_BLOCK = WeightedList.<Block>builder().add(Blocks.VINE).build();

    public static final Codec<VinesConfig> CODEC = RecordCodecBuilder.<VinesConfig>create(instance -> instance.group(
        LithostitchedCodecs.compactWeightedList(BuiltInRegistries.BLOCK.byNameCodec(), false).fieldOf("block").orElse(DEFAULT_BLOCK).forGetter(VinesConfig::blocks),
        LithostitchedCodecs.BLOCK_SET.optionalFieldOf("can_place_on").forGetter(VinesConfig::canPlaceOn),
        IntProviders.codec(1, 256).fieldOf("max_length").orElse(ConstantInt.of(1)).forGetter(VinesConfig::maxLength)
    ).apply(instance, VinesConfig::new)).validate(VinesConfig::validate);

    private DataResult<VinesConfig> validate() {
        /*if (this.blocks.unwrap().stream().map(Weighted::value).anyMatch(block -> !(block instanceof VineBlock))) {
            return DataResult.error(() -> "State should be a vine block");
        }*/
        return DataResult.success(this);
    }

    public boolean canPlaceOn(BlockState state) {
        return this.canPlaceOn.isEmpty() || state.is(this.canPlaceOn.get());
    }
}
