package dev.worldgen.lithostitched.worldgen.structure;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.structure.condition.AllOfStructureCondition;
import dev.worldgen.lithostitched.worldgen.structure.condition.StructureCondition;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Arrays;

public final class DelegatingConfig {
    public static final MapCodec<DelegatingConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Structure.CODEC.fieldOf("delegate").forGetter(DelegatingConfig::delegate),
        StructureCondition.CODEC.fieldOf("spawn_condition").forGetter(DelegatingConfig::spawnCondition)
    ).apply(instance, DelegatingConfig::new));
    private final Holder<Structure> delegate;
    private StructureCondition spawnCondition;

    public DelegatingConfig(Holder<Structure> delegate, StructureCondition spawnCondition) {
        this.delegate = delegate;
        this.spawnCondition = spawnCondition;
    }

    public Holder<Structure> delegate() {
        return delegate;
    }

    public StructureCondition spawnCondition() {
        return spawnCondition;
    }

    public void setSpawnCondition(StructureCondition spawnCondition, boolean append) {
        if (append) {
            if (this.spawnCondition instanceof AllOfStructureCondition all) {
                all.appendCondition(spawnCondition);
            } else {
                this.spawnCondition = new AllOfStructureCondition(Arrays.asList(this.spawnCondition, spawnCondition));
            }
        } else {
            this.spawnCondition = spawnCondition;
        }
    }
}
