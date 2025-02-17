package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.JigsawStructureAccessor;
import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawStructure;
import dev.worldgen.lithostitched.worldgen.structure.DelegatingStructure;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.ArrayList;
import java.util.List;

import static dev.worldgen.lithostitched.worldgen.LithostitchedCodecs.registrySet;

public record SetPoolAliasesModifier(HolderSet<Structure> structures, List<PoolAliasBinding> poolAliases, boolean append) implements Modifier {
    public static final MapCodec<SetPoolAliasesModifier> CODEC = RecordCodecBuilder.<SetPoolAliasesModifier>mapCodec(instance -> instance.group(
        registrySet(Registries.STRUCTURE, "structures").forGetter(SetPoolAliasesModifier::structures),
        Codec.list(PoolAliasBinding.CODEC).fieldOf("pool_aliases").forGetter(SetPoolAliasesModifier::poolAliases),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetPoolAliasesModifier::append)
    ).apply(instance, SetPoolAliasesModifier::new)).validate(SetPoolAliasesModifier::validate);

    private static DataResult<SetPoolAliasesModifier> validate(SetPoolAliasesModifier modifier) {
        for (Holder<Structure> holder : modifier.structures) {
            Structure structure = holder.value();
            if (!(structure instanceof JigsawStructure || structure instanceof AlternateJigsawStructure)) {
                return DataResult.error(() -> "Target structure for pool alias additions should be a jigsaw structure");
            }
        }
        return DataResult.success(modifier);
    }

    @Override
    public ModifierPhase getPhase() {
        return this.append ? ModifierPhase.REPLACE : ModifierPhase.ADD;
    }

    @Override
    public void applyModifier() {
        this.structures.stream().map(Holder::value).forEach(this::applyModifier);
    }

    private void applyModifier(Structure structure) {
        if (structure instanceof DelegatingStructure delegating) {
            structure = delegating.delegate();
        }

        if (structure instanceof AlternateJigsawStructure alternateJigsaw) {
            alternateJigsaw.setPoolAliases(this.poolAliases, this.append);
        } else {
            List<PoolAliasBinding> mergedAliases = new ArrayList<>();
            if (this.append) mergedAliases.addAll(((JigsawStructureAccessor)structure).getPoolAliases());

            mergedAliases.addAll(this.poolAliases);
            ((JigsawStructureAccessor)structure).setPoolAliases(mergedAliases);
        }
    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
