package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.placementcondition.AllOfPlacementCondition;
import dev.worldgen.lithostitched.api.worldgen.placementcondition.PlacementCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Optional;

public final class DelegatingConfig {
    public static final MapCodec<DelegatingConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Structure.CODEC.fieldOf("delegate").forGetter(DelegatingConfig::delegate),
        PlacementCondition.CODEC.optionalFieldOf("spawn_condition").forGetter(DelegatingConfig::spawnCondition),
        EnvironmentAttributeMap.CODEC_ONLY_POSITIONAL.optionalFieldOf("attributes", EnvironmentAttributeMap.EMPTY).forGetter(DelegatingConfig::attributes)
    ).apply(instance, DelegatingConfig::new));
    
    private final Holder<Structure> delegate;
    private Optional<PlacementCondition> spawnCondition;
    @NotNull
    private EnvironmentAttributeMap attributes;
    
    public DelegatingConfig(Holder<Structure> delegate, Optional<PlacementCondition> spawnCondition) {
        this(delegate, spawnCondition, EnvironmentAttributeMap.EMPTY);
    }

    public DelegatingConfig(Holder<Structure> delegate, Optional<PlacementCondition> spawnCondition, @NonNull EnvironmentAttributeMap attributes) {
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
    
    public EnvironmentAttributeMap attributes() {
        return this.attributes;
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
    
    public void setAttributes(EnvironmentAttributeMap attributes, boolean append) {
        var builder = EnvironmentAttributeMap.builder();
        if (append) {
            builder.putAll(this.attributes);
        }
        builder.putAll(attributes);
        this.attributes = builder.build();
    }
}
