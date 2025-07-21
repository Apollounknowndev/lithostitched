package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.placementcondition.AllOfPlacementCondition;
import dev.worldgen.lithostitched.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Arrays;

public final class DelegatingConfig {
    public static final MapCodec<DelegatingConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Structure.CODEC.fieldOf("delegate").forGetter(DelegatingConfig::delegate),
        PlacementCondition.CODEC.fieldOf("spawn_condition").forGetter(DelegatingConfig::spawnCondition)
    ).apply(instance, DelegatingConfig::new));
    private final Holder<Structure> delegate;
    private PlacementCondition spawnCondition;

    public DelegatingConfig(Holder<Structure> delegate, PlacementCondition spawnCondition) {
        this.delegate = delegate;
        this.spawnCondition = spawnCondition;
    }

    public Holder<Structure> delegate() {
        return delegate;
    }

    public PlacementCondition spawnCondition() {
        return spawnCondition;
    }

    public void setSpawnCondition(PlacementCondition spawnCondition, boolean append) {
        if (append) {
            if (this.spawnCondition instanceof AllOfPlacementCondition all) {
                all.appendCondition(spawnCondition);
            } else {
                this.spawnCondition = new AllOfPlacementCondition(Arrays.asList(this.spawnCondition, spawnCondition));
            }
        } else {
            this.spawnCondition = spawnCondition;
        }
    }
}
