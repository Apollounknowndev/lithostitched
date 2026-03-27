package dev.worldgen.lithostitched.worldgen.stateprovider;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.api.util.WeightedList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import org.jetbrains.annotations.NotNull;

public final class WeightedProvider extends BlockStateProvider {
    public static final MapCodec<WeightedProvider> CODEC = WeightedList.codec(BlockStateProvider.CODEC).fieldOf("entries").xmap(WeightedProvider::new, WeightedProvider::providers);
    public static final BlockStateProviderType<WeightedProvider> TYPE = new BlockStateProviderType<>(CODEC);

    private final WeightedList<BlockStateProvider> providers;

    public WeightedProvider(WeightedList<BlockStateProvider> providers) {
        this.providers = providers;
    }

    public WeightedList<BlockStateProvider> providers() {
        return providers;
    }

    @Override
    @NotNull
    protected BlockStateProviderType<?> type() {
        return TYPE;
    }

    @Override
    @NotNull
    public BlockState getState(@NotNull RandomSource random, @NotNull BlockPos pos) {
        return this.providers.getRandom(random).map(provider -> provider.getState(random, pos)).orElse(Blocks.AIR.defaultBlockState());
    }
}
