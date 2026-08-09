package dev.worldgen.lithostitched.impl.worldgen.modifier.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.impl.Lithostitched;
import dev.worldgen.lithostitched.api.predicate.LoadPredicate;
import dev.worldgen.lithostitched.api.worldgen.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.DimensionTypeAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Optional;

public record SetDimensionAttributesModifier(Optional<LoadPredicate> predicate, int priority, HolderSet<DimensionType> dimensionTypes, EnvironmentAttributeMap attributes, boolean append) implements WorldgenModifier {
    public static final MapCodec<SetDimensionAttributesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        LoadPredicate.FIELD_CODEC.forGetter(WorldgenModifier::predicate),
        PRIORITY_DEFAULT_CODEC.forGetter(SetDimensionAttributesModifier::priority),
        RegistryCodecs.holderSet(Registries.DIMENSION_TYPE).fieldOf("dimension_types").forGetter(SetDimensionAttributesModifier::dimensionTypes),
        EnvironmentAttributeMap.CODEC.fieldOf("attributes").forGetter(SetDimensionAttributesModifier::attributes),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetDimensionAttributesModifier::append)
    ).apply(instance, SetDimensionAttributesModifier::new));

    @Override
    public void apply(RegistryAccess registries) {
        for (Holder<DimensionType> dimensionType : this.dimensionTypes) {
            var builder = EnvironmentAttributeMap.builder();
            if (this.append) {
                builder.putAll(dimensionType.value().attributes());
            }
            builder.putAll(this.attributes);

            ((DimensionTypeAccessor)(Object)dimensionType.value()).setAttributes(builder.build());
            WorldgenModifier.resetRegistrationInfo(Lithostitched.registry(registries, Registries.DIMENSION_TYPE), dimensionType);
        }
    }

    @Override
    public MapCodec<? extends WorldgenModifier> codec() {
        return CODEC;
    }
}
