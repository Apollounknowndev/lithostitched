package dev.worldgen.lithostitched.worldgen.feature.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public record CompositeConfig(HolderSet<PlacedFeature> features, Type placementType) implements FeatureConfiguration {
    public static final Codec<CompositeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        PlacedFeature.LIST_CODEC.fieldOf("features").forGetter(CompositeConfig::features),
        Type.CODEC.fieldOf("placement_type").orElse(Type.NEVER_CANCEL).forGetter(CompositeConfig::placementType)
    ).apply(instance, CompositeConfig::new));

    public enum Type implements StringRepresentable {
        NEVER_CANCEL("never_cancel", success -> true),
        CANCEL_ON_FAILURE("cancel_on_failure", success -> success),
        CANCEL_ON_SUCCESS("cancel_on_success", success -> !success),;

        public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

        private final String name;
        private final Predicate<Boolean> continueCondition;

        Type(String name, Predicate<Boolean> continueCondition) {
            this.name = name;
            this.continueCondition = continueCondition;
        }

        @Override
        @NotNull
        public String getSerializedName() {
            return this.name;
        }

        public boolean shouldContinue(boolean success) {
            return continueCondition.test(success);
        }
    }
}
