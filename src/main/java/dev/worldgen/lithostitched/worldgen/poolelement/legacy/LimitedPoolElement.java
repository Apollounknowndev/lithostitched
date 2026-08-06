package dev.worldgen.lithostitched.worldgen.poolelement.legacy;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

import java.util.Optional;

public class LimitedPoolElement extends DelegatingPoolElement {
    public static final MapCodec<LimitedPoolElement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StructurePoolElement.CODEC.fieldOf("delegate").forGetter(DelegatingPoolElement::delegate),
        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_depth").forGetter(DelegatingPoolElement::minDepth),
        ExtraCodecs.POSITIVE_INT.fieldOf("limit").forGetter(LimitedPoolElement::limit)
    ).apply(instance, LimitedPoolElement::new));
    public static final StructurePoolElementType<LimitedPoolElement> TYPE = () -> CODEC;
    private final int limit;

    public LimitedPoolElement(StructurePoolElement delegate, Optional<Integer> minDepth, int limit) {
        super(delegate, minDepth, Optional.empty(), Optional.of(limit));
        this.limit = limit;
    }

    private int limit() {
        return this.limit;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return TYPE;
    }
}
