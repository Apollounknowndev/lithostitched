package dev.worldgen.lithostitched.worldgen.poolelement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public class GuaranteedPoolElement extends ExclusivePoolElement {
    public static final MapCodec<GuaranteedPoolElement> CODEC = RecordCodecBuilder.mapCodec(instance -> addFields(instance).and(
        ExtraCodecs.POSITIVE_INT.fieldOf("count").forGetter(GuaranteedPoolElement::count)
    ).apply(instance, GuaranteedPoolElement::new));
    public static final StructurePoolElementType<GuaranteedPoolElement> TYPE = () -> CODEC;
    private final int count;

    public GuaranteedPoolElement(StructurePoolElement delegate, int minDepth, int count) {
        super(delegate, minDepth);
        this.count = count;
    }

    public int count() {
        return this.count;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return TYPE;
    }
}
