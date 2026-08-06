//? if fabric {
/*package dev.worldgen.lithostitched.platform.fabric.resource;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.Lithostitched;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.resources.RegistryOps;
import org.jetbrains.annotations.Nullable;

public class BreaksSeedParityCondition implements ResourceCondition {
    public static final MapCodec<BreaksSeedParityCondition> CODEC = MapCodec.unit(BreaksSeedParityCondition::new);
    public static final ResourceConditionType<BreaksSeedParityCondition> TYPE = ResourceConditionType.create(Lithostitched.id("breaks_seed_parity"), CODEC);

    @Override
    public ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(@Nullable RegistryOps.RegistryInfoLookup registryLookup) {
        return Lithostitched.breaksSeedParity();
    }
}*///? }