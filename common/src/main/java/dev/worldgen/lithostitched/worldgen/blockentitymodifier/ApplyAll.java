package dev.worldgen.lithostitched.worldgen.blockentitymodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record ApplyAll(List<RuleBlockEntityModifier> modifiers) implements RuleBlockEntityModifier {
    public static final Codec<ApplyAll> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        RuleBlockEntityModifier.CODEC.listOf().fieldOf("modifiers").forGetter(ApplyAll::modifiers)
    ).apply(instance, ApplyAll::new));

    public static final RuleBlockEntityModifierType<ApplyAll> APPLY_ALL_TYPE = () -> CODEC;

    @Nullable
    @Override
    public CompoundTag apply(@NotNull RandomSource randomSource, @Nullable CompoundTag compoundTag) {
        for (RuleBlockEntityModifier modifier : modifiers) {
            compoundTag = modifier.apply(randomSource, compoundTag);
        }
        return compoundTag;
    }

    @Override
    public @NotNull RuleBlockEntityModifierType<?> getType() {
        return APPLY_ALL_TYPE;
    }
}
