package dev.worldgen.lithostitched.worldgen.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.mixin.common.JigsawStructureAccessor;
import dev.worldgen.lithostitched.worldgen.structure.AlternateJigsawStructure;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.ArrayList;
import java.util.List;

public record SetPoolAliasesModifier(Holder<Structure> structure, List<PoolAliasBinding> poolAliases, boolean append) implements Modifier {
    public static final MapCodec<SetPoolAliasesModifier> CODEC = RecordCodecBuilder.<SetPoolAliasesModifier>mapCodec(instance -> instance.group(
        Structure.CODEC.fieldOf("structure").forGetter(SetPoolAliasesModifier::structure),
        Codec.list(PoolAliasBinding.CODEC).fieldOf("pool_aliases").forGetter(SetPoolAliasesModifier::poolAliases),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetPoolAliasesModifier::append)
    ).apply(instance, SetPoolAliasesModifier::new)).validate(SetPoolAliasesModifier::validate);

    private static DataResult<SetPoolAliasesModifier> validate(SetPoolAliasesModifier modifier) {
        Structure structure = modifier.structure.value();
        if (!(structure instanceof JigsawStructure || structure instanceof AlternateJigsawStructure)) {
            return DataResult.error(() -> "Target structure for pool alias additions should be a jigsaw structure");
        }
        return DataResult.success(modifier);
    }

    @Override
    public ModifierPhase getPhase() {
        return ModifierPhase.ADD;
    }

    @Override
    public void applyModifier() {
        Structure structure = this.structure.value();

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
