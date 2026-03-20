package dev.worldgen.lithostitched.util.weighted;

import dev.worldgen.lithostitched.api.util.Weighted;
import dev.worldgen.lithostitched.api.util.WeightedList;
import net.msrandom.classextensions.ClassExtension;
import net.msrandom.classextensions.ExtensionInject;
import net.msrandom.classextensions.ExtensionShadow;

import java.util.List;

@ClassExtension(WeightedList.class)
public class WeightedListExtension<E> {
    @ExtensionShadow
    private final List<Weighted<E>> items;

    @ExtensionInject
    public net.minecraft.util.random.WeightedList<E> toVanilla() {
        return net.minecraft.util.random.WeightedList.of(this.items.stream().map(weighted -> new net.minecraft.util.random.Weighted<>(weighted.value(), weighted.weight())).toList());
    }
}
