package dev.worldgen.lithostitched.resource;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.config.ConfigHandler;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

import static dev.worldgen.lithostitched.LithostitchedCommon.id;

public class BreaksSeedParityCondition implements ResourceCondition {
    public static final MapCodec<BreaksSeedParityCondition> CODEC = MapCodec.unit(BreaksSeedParityCondition::new);
    public static final ResourceConditionType<BreaksSeedParityCondition> TYPE = ResourceConditionType.create(id("breaks_seed_parity"), CODEC);

    @Override
    public ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(@Nullable HolderLookup.Provider registryLookup) {
        return ConfigHandler.getConfig().breaksSeedParity();
    }
}
