package dev.worldgen.lithostitched.worldgen.blockentitymodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ApplyRandom(SimpleWeightedRandomList<RuleBlockEntityModifier> modifiers) implements RuleBlockEntityModifier {
    public static final Codec<ApplyRandom> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SimpleWeightedRandomList.wrappedCodec(RuleBlockEntityModifier.CODEC).fieldOf("modifiers").forGetter(ApplyRandom::modifiers)
    ).apply(instance, ApplyRandom::new));

    public static final RuleBlockEntityModifierType<ApplyRandom> APPLY_RANDOM_TYPE = () -> CODEC;

    @Nullable
    @Override
    public CompoundTag apply(@NotNull RandomSource randomSource, @Nullable CompoundTag compoundTag) {
        Optional<RuleBlockEntityModifier> modifier = modifiers.getRandomValue(randomSource);
        if (modifier.isPresent()) {
            return modifier.get().apply(randomSource, compoundTag);
        }
        return compoundTag;
    }

    @Override
    public @NotNull RuleBlockEntityModifierType<?> getType() {
        return APPLY_RANDOM_TYPE;
    }
}
