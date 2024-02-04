package dev.worldgen.lithostitched.worldgen.poolelement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public class GuaranteedPoolElement extends ExclusivePoolElement {
    public static final Codec<GuaranteedPoolElement> CODEC = RecordCodecBuilder.create(instance -> addDelegateField(instance).and(instance.group(
        ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(GuaranteedPoolElement::count),
        ExtraCodecs.NON_NEGATIVE_INT.fieldOf("min_depth").forGetter(GuaranteedPoolElement::minDepth)
    )).apply(instance, GuaranteedPoolElement::new));
    public static final StructurePoolElementType<GuaranteedPoolElement> GUARANTEED_TYPE = () -> CODEC;
    private final int count;
    private final int minDepth;

    protected GuaranteedPoolElement(StructurePoolElement delegate, int count, int minDepth) {
        super(delegate);
        this.count = count;
        this.minDepth = minDepth;
    }

    public int count() {
        return this.count;
    }

    public int minDepth() {
        return this.minDepth;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return GUARANTEED_TYPE;
    }
}
