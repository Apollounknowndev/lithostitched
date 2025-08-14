package dev.worldgen.lithostitched.worldgen.placementcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.platform.Services;
import net.minecraft.core.BlockPos;

public record ModLoadedPlacementCondition(String mod) implements PlacementCondition {
    public static final MapCodec<ModLoadedPlacementCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.string(2, 64).fieldOf("mod").forGetter(ModLoadedPlacementCondition::mod)
    ).apply(instance, ModLoadedPlacementCondition::new));

    @Override
    public boolean test(Context context, BlockPos pos) {
        return Services.PLATFORM.isModLoaded(mod);
    }

    @Override
    public MapCodec<? extends PlacementCondition> codec() {
        return CODEC;
    }
}
