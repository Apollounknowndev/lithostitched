package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.placementcondition.AllOfPlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Arrays;
import java.util.Optional;

public final class DelegatingConfig {
    public static final MapCodec<DelegatingConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Structure.CODEC.fieldOf("delegate").forGetter(DelegatingConfig::delegate),
        PlacementCondition.CODEC.optionalFieldOf("spawn_condition").forGetter(DelegatingConfig::spawnCondition)
    ).apply(instance, DelegatingConfig::new));
    
    private final Holder<Structure> delegate;
    private Optional<PlacementCondition> spawnCondition;
    
    public DelegatingConfig(Holder<Structure> delegate, Optional<PlacementCondition> spawnCondition) {
        this.delegate = delegate;
        this.spawnCondition = spawnCondition;
    }

    public Holder<Structure> delegate() {
        return delegate;
    }

    public Optional<PlacementCondition> spawnCondition() {
        return spawnCondition;
    }

    public void setSpawnCondition(PlacementCondition spawnCondition, boolean append) {
        if (append) {
            if (this.spawnCondition.isPresent()) {
                if (this.spawnCondition.get() instanceof AllOfPlacementCondition all) {
                    all.appendCondition(spawnCondition);
                } else {
                    this.spawnCondition = Optional.of(new AllOfPlacementCondition(Arrays.asList(this.spawnCondition.get(), spawnCondition)));
                }
            } else {
                this.spawnCondition = Optional.of(spawnCondition);
            }
        } else {
            this.spawnCondition = Optional.of(spawnCondition);
        }
    }
}
