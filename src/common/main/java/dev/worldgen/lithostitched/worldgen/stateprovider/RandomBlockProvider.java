package dev.worldgen.lithostitched.worldgen.stateprovider;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.LithostitchedCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import org.jetbrains.annotations.NotNull;

public final class RandomBlockProvider extends BlockStateProvider {
    public static final MapCodec<RandomBlockProvider> CODEC = LithostitchedCodecs.BLOCK_SET.fieldOf("blocks").xmap(RandomBlockProvider::new, RandomBlockProvider::blocks);
    public static final BlockStateProviderType<RandomBlockProvider> TYPE = new BlockStateProviderType<>(CODEC);

    private final HolderSet<Block> blocks;

    public RandomBlockProvider(HolderSet<Block> blocks) {
        this.blocks = blocks;
    }

    public HolderSet<Block> blocks() {
        return blocks;
    }

    @Override
    @NotNull
    protected BlockStateProviderType<?> type() {
        return TYPE;
    }

    @Override
    @NotNull
    public BlockState getState(@NotNull RandomSource random, @NotNull BlockPos pos) {
        return this.blocks.getRandomElement(random).map(Holder::value).orElse(Blocks.AIR).defaultBlockState();
    }
}
