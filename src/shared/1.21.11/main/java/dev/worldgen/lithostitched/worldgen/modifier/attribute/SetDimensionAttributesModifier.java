package dev.worldgen.lithostitched.worldgen.modifier.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.lithostitched.Lithostitched;
import dev.worldgen.lithostitched.api.modifier.WorldgenModifier;
import dev.worldgen.lithostitched.mixin.common.DimensionTypeAccessor;
import dev.worldgen.lithostitched.worldgen.modifier.Modifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.level.dimension.DimensionType;

public record SetDimensionAttributesModifier(int priority, HolderSet<DimensionType> dimensionTypes, EnvironmentAttributeMap attributes, boolean append) implements Modifier {
    public static final MapCodec<SetDimensionAttributesModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        PRIORITY_DEFAULT.forGetter(SetDimensionAttributesModifier::priority),
        RegistryCodecs.homogeneousList(Registries.DIMENSION_TYPE).fieldOf("dimension_types").forGetter(SetDimensionAttributesModifier::dimensionTypes),
        EnvironmentAttributeMap.CODEC.fieldOf("attributes").forGetter(SetDimensionAttributesModifier::attributes),
        Codec.BOOL.fieldOf("append").orElse(true).forGetter(SetDimensionAttributesModifier::append)
    ).apply(instance, SetDimensionAttributesModifier::new));

    @Override
    public void applyModifier(RegistryAccess registries) {
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
    public void applyModifier() {

    }

    @Override
    public MapCodec<? extends Modifier> codec() {
        return CODEC;
    }
}
