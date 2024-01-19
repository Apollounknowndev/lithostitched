package dev.worldgen.lithostitched.worldgen.poolelement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

public class LimitedPoolElement extends ExclusivePoolElement {
    public static final Codec<LimitedPoolElement> CODEC = RecordCodecBuilder.create(instance -> addDelegateField(instance).and(
        ExtraCodecs.POSITIVE_INT.fieldOf("limit").forGetter(LimitedPoolElement::limit)
    ).apply(instance, LimitedPoolElement::new));
    public static final StructurePoolElementType<LimitedPoolElement> LIMITED_TYPE = () -> CODEC;
    private final int limit;

    protected LimitedPoolElement(StructurePoolElement delegate, int limit) {
        super(delegate);
        this.limit = limit;
    }

    public int limit() {
        return this.limit;
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return LIMITED_TYPE;
    }
}
