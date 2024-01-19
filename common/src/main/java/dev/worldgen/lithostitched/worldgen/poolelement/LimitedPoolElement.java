package dev.worldgen.lithostitched.worldgen.poolelement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Collections;
import java.util.List;

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
