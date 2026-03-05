package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.MapCodec;
import dev.worldgen.lithostitched.worldgen.placementcondition.AllOfPlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.msrandom.multiplatform.annotations.Expect;

import java.util.Arrays;
import java.util.Optional;

public final class DelegatingConfig {
    private final Holder<Structure> delegate;
    private Optional<PlacementCondition> spawnCondition;
    private Object attributes;
    
    public DelegatingConfig(Holder<Structure> delegate, Optional<PlacementCondition> spawnCondition) {
        this(delegate, spawnCondition, null);
    }

    public DelegatingConfig(Holder<Structure> delegate, Optional<PlacementCondition> spawnCondition, Object attributes) {
        this.delegate = delegate;
        this.spawnCondition = spawnCondition;
        this.attributes = attributes;
    }

    public Holder<Structure> delegate() {
        return delegate;
    }

    public Optional<PlacementCondition> spawnCondition() {
        return spawnCondition;
    }
    
    public Object getAttributes() {
        return this.attributes;
    }
    
    public void setAttributes(Object attributes) {
        this.attributes = attributes;
    }
    
    @Expect
    public static MapCodec<DelegatingConfig> getCodec();

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
