package dev.worldgen.lithostitched.worldgen.ruletest;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

// Backport from 1.21 for codec-ified block property predicates.
public record AlternatePropertiesPredicate(List<PropertyMatcher> properties) {
    private static final Codec<List<PropertyMatcher>> PROPERTIES_CODEC = Codec.unboundedMap(Codec.STRING, ValueMatcher.CODEC).orElse(Map.of()).xmap(
        map -> map.entrySet().stream().map(PropertyMatcher::new).toList(),
        matchers -> matchers.stream().collect(Collectors.toMap(PropertyMatcher::name, PropertyMatcher::valueMatcher))
    );

    public static final Codec<AlternatePropertiesPredicate> CODEC = PROPERTIES_CODEC.xmap(AlternatePropertiesPredicate::new, AlternatePropertiesPredicate::properties);

    public <S extends StateHolder<?, S>> boolean matches(StateDefinition<?, S> $$0, S $$1) {
        for (PropertyMatcher matcher : this.properties) {
            if (!matcher.match($$0, $$1)) {
                return false;
            }
        }

        return true;
    }

    public boolean matches(BlockState $$0) {
        return this.matches($$0.getBlock().getStateDefinition(), $$0);
    }

    private record PropertyMatcher(String name, ValueMatcher valueMatcher) {
        public PropertyMatcher(Map.Entry<String, ValueMatcher> map) {
            this(map.getKey(), map.getValue());
        }

        public <S extends StateHolder<?, S>> boolean match(StateDefinition<?, S> stateDefinition, S $$1) {
            Property<?> $$2 = stateDefinition.getProperty(this.name);
            return $$2 != null && this.valueMatcher.match($$1, $$2);
        }
    }

    private interface ValueMatcher {
        Codec<ValueMatcher> CODEC = Codec.either(ExactMatcher.CODEC, RangedMatcher.CODEC).xmap(either -> either.map(Function.identity(), Function.identity()), (matcher) -> {
            if (matcher instanceof ExactMatcher exactMatcher) {
                return Either.left(exactMatcher);
            } else if (matcher instanceof RangedMatcher rangedMatcher) {
                return Either.right(rangedMatcher);
            } else {
                throw new UnsupportedOperationException();
            }
        });

        <T extends Comparable<T>> boolean match(StateHolder<?, ?> stateHolder, Property<T> property);
    }

    private record RangedMatcher(Optional<String> minValue, Optional<String> maxValue) implements ValueMatcher {
        public static final Codec<RangedMatcher> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("min").forGetter(RangedMatcher::minValue),
            Codec.STRING.optionalFieldOf("max").forGetter(RangedMatcher::maxValue)
        ).apply(instance, RangedMatcher::new));

        public <T extends Comparable<T>> boolean match(StateHolder<?, ?> stateHolder, Property<T> property) {
            T $$2 = stateHolder.getValue(property);
            Optional<T> $$4;
            if (this.minValue.isPresent()) {
                $$4 = property.getValue(this.minValue.get());
                if ($$4.isEmpty() || $$2.compareTo($$4.get()) < 0) {
                    return false;
                }
            }

            if (this.maxValue.isPresent()) {
                $$4 = property.getValue(this.maxValue.get());
                return $$4.isPresent() && $$2.compareTo($$4.get()) <= 0;
            }

            return true;
        }
    }

    private record ExactMatcher(String value) implements ValueMatcher {
        public static final Codec<ExactMatcher> CODEC = Codec.STRING.xmap(ExactMatcher::new, ExactMatcher::value);
        public <T extends Comparable<T>> boolean match(StateHolder<?, ?> stateHolder, Property<T> property) {
            T $$2 = stateHolder.getValue(property);
            Optional<T> $$3 = property.getValue(this.value);
            return $$3.isPresent() && $$2.compareTo($$3.get()) == 0;
        }
    }
}
