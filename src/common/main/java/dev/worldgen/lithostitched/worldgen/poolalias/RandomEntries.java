package dev.worldgen.lithostitched.worldgen.poolalias;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.HolderSetCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Hacky pool alias binding for Trial Chambers. Please don't use.
 */
public record RandomEntries(List<ResourceKey<StructureTemplatePool>> aliases, List<HolderSet<StructureTemplatePool>> pools) implements PoolAliasBinding {
    public static final MapCodec<RandomEntries> CODEC = RecordCodecBuilder.<RandomEntries>mapCodec(instance -> instance.group(
        ResourceKey.codec(Registries.TEMPLATE_POOL).listOf().fieldOf("aliases").forGetter(RandomEntries::aliases),
        HolderSetCodec.create(Registries.TEMPLATE_POOL, StructureTemplatePool.CODEC, false).listOf().fieldOf("pools").forGetter(RandomEntries::pools)
    ).apply(instance, RandomEntries::new)).validate(RandomEntries::validate);


    private static DataResult<RandomEntries> validate(RandomEntries entry) {
        if (entry.pools.size() == entry.aliases.size()) {
            Integer size = null;

            for (HolderSet<StructureTemplatePool> pool : entry.pools) {
                if (size != null) {
                    if (pool.size() != size) return DataResult.error(() -> "Each template pool set should have the same number of entries");
                }
                size = pool.size();
            }

            return DataResult.success(entry);
        }
        return DataResult.error(() -> "List of aliases and list of pools should be the same length");
    }


    @Override
    public void forEachResolved(RandomSource random, BiConsumer<ResourceKey<StructureTemplatePool>, ResourceKey<StructureTemplatePool>> consumer) {
        int index = random.nextInt(pools.getFirst().size());
        for (int i = 0; i < pools.size(); i++) {
            consumer.accept(aliases.get(i), pools.get(i).get(index).unwrapKey().get());
        }
    }

    // This should never run, it appears it's only used in vanilla data generation.
    @Override
    public Stream<ResourceKey<StructureTemplatePool>> allTargets() {
        return Stream.of();
    }

    @Override
    public MapCodec<? extends PoolAliasBinding> codec() {
        return CODEC;
    }
}
