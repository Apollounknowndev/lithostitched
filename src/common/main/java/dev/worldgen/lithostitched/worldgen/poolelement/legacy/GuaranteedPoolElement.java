package dev.worldgen.lithostitched.worldgen.poolelement.legacy;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.worldgen.poolelement.DelegatingPoolElement;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

import java.util.Optional;

public class GuaranteedPoolElement extends DelegatingPoolElement {
    public static final MapCodec<GuaranteedPoolElement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        StructurePoolElement.CODEC.fieldOf("delegate").forGetter(DelegatingPoolElement::delegate),
        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("min_depth").forGetter(DelegatingPoolElement::minDepth),
        ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(GuaranteedPoolElement::count)
    ).apply(instance, GuaranteedPoolElement::new));
    public static final StructurePoolElementType<GuaranteedPoolElement> TYPE = () -> CODEC;
    private final int count;

    public GuaranteedPoolElement(StructurePoolElement delegate, Optional<Integer> minDepth, int count) {
        super(delegate, minDepth, Optional.of(count), Optional.empty());
        this.count = count;
    }

    private int count() {
        return this.count;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return TYPE;
    }
}
