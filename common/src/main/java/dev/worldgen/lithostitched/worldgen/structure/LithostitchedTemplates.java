package dev.worldgen.lithostitched.worldgen.structure;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import dev.worldgen.lithostitched.worldgen.poolelement.ExclusivePoolElement;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class LithostitchedTemplates implements Iterable<StructurePoolElement> {
    protected final List<WeightedEntry> entries;

    public LithostitchedTemplates() {
        this.entries = Lists.newArrayList();
    }

    public LithostitchedTemplates add(StructurePoolElement element, int weight) {
        this.entries.add(new WeightedEntry(element, this.entries.size(), weight));
        return this;
    }

    public List<StructurePoolElement> shuffle(RandomSource random, int depth) {
        List<WeightedEntry> shuffled = Lists.newArrayList(this.entries.stream().map(WeightedEntry::copy).toList());
        shuffled.forEach(entry -> entry.setRandom(random.nextFloat(), depth));
        shuffled.sort(Comparator.comparingDouble(WeightedEntry::getRandWeight));

        return shuffled.stream().map(WeightedEntry::getData).toList();
    }

    public Stream<StructurePoolElement> stream() {
        return this.entries.stream().map(WeightedEntry::getData);
    }

    @Override
    @NotNull
    public Iterator<StructurePoolElement> iterator() {
        return Iterators.transform(this.entries.iterator(), WeightedEntry::getData);
    }

    public static class WeightedEntry {
        final StructurePoolElement data;
        final int index;
        final int weight;
        private double randWeight;
        private final boolean guaranteed;

        WeightedEntry(StructurePoolElement element, int index, int weight) {
            this.data = element;
            this.index = index;
            this.weight = weight;
            this.guaranteed = element instanceof ExclusivePoolElement;
        }

        private WeightedEntry copy() {
            return new WeightedEntry(this.data, this.index, this.weight);
        }

        private double getRandWeight() {
            return this.randWeight;
        }

        void setRandom(float value, int depth) {
            this.randWeight = -Math.pow(value, (1.0F / (float) this.weight)) + getOffset(depth);
        }

        private double getOffset(int depth) {
            return this.guaranteed && depth >= ((ExclusivePoolElement)data).minDepth() ? -2 : 0;
        }

        public StructurePoolElement getData() {
            return this.data;
        }

        public int getIndex() {
            return this.index;
        }

        @Override
        public String toString() {
            return this.weight + ":" + this.data;
        }
    }
}

