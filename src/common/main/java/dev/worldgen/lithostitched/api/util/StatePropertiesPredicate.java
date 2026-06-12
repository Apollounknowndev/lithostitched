package dev.worldgen.lithostitched.api.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;

/**
 * Version-agnostic predicate for testing block/fluid states.
 * <p>
 * Mojang likes to keep moving this class, so this version keeps everything consistent between versions.
 */
public record StatePropertiesPredicate(List<PropertyMatcher> properties) {
	private static final Codec<List<PropertyMatcher>> PROPERTIES_CODEC = Codec.unboundedMap(Codec.STRING, ValueMatcher.CODEC).xmap(
		map -> map.entrySet().stream().map(entry -> new PropertyMatcher(entry.getKey(), entry.getValue())).toList(),
		properties -> properties.stream().collect(Collectors.toMap(PropertyMatcher::name, PropertyMatcher::valueMatcher)));
	public static final Codec<StatePropertiesPredicate> CODEC = PROPERTIES_CODEC.xmap(StatePropertiesPredicate::new, StatePropertiesPredicate::properties);
	public static final StreamCodec<ByteBuf, StatePropertiesPredicate> STREAM_CODEC = PropertyMatcher.STREAM_CODEC
		.apply(ByteBufCodecs.list())
		.map(StatePropertiesPredicate::new, StatePropertiesPredicate::properties);
	
	public <S extends StateHolder<?, S>> boolean matches(final StateDefinition<?, S> definition, final S state) {
		for (PropertyMatcher matcher : this.properties) {
			if (!matcher.match(definition, state)) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean matches(final BlockState state) {
		return this.matches(state.getBlock().getStateDefinition(), state);
	}
	
	public boolean matches(final FluidState state) {
		return this.matches(state.getType().getStateDefinition(), state);
	}
	
	public Optional<String> checkState(final StateDefinition<?, ?> states) {
		for (PropertyMatcher property : this.properties) {
			Optional<String> unknownProperty = property.checkState(states);
			if (unknownProperty.isPresent()) {
				return unknownProperty;
			}
		}
		
		return Optional.empty();
	}
	
	public static class Builder {
		private final com.google.common.collect.ImmutableList.Builder<PropertyMatcher> matchers = ImmutableList.builder();
		
		private Builder() {
		}
		
		public static Builder properties() {
			return new Builder();
		}
		
		public Builder hasProperty(final Property<?> property, final String value) {
			this.matchers.add(new PropertyMatcher(property.getName(), new ExactMatcher(value)));
			return this;
		}
		
		public Builder hasProperty(final Property<Integer> property, final int value) {
			return this.hasProperty(property, Integer.toString(value));
		}
		
		public Builder hasProperty(final Property<Boolean> property, final boolean value) {
			return this.hasProperty(property, Boolean.toString(value));
		}
		
		public <T extends Comparable<T> & StringRepresentable> Builder hasProperty(final Property<T> property, final T value) {
			return this.hasProperty(property, value.getSerializedName());
		}
		
		public Optional<StatePropertiesPredicate> build() {
			return Optional.of(new StatePropertiesPredicate(this.matchers.build()));
		}
	}
	
	private record ExactMatcher(String value) implements ValueMatcher {
		public static final Codec<ExactMatcher> CODEC = Codec.STRING.xmap(ExactMatcher::new, ExactMatcher::value);
		public static final StreamCodec<ByteBuf, ExactMatcher> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.map(ExactMatcher::new, ExactMatcher::value);
		
		@Override
		public <T extends Comparable<T>> boolean match(final StateHolder<?, ?> state, final Property<T> property) {
			T actualValue = state.getValue(property);
			Optional<T> typedExpected = property.getValue(this.value);
			return typedExpected.isPresent() && actualValue.compareTo(typedExpected.get()) == 0;
		}
	}
	
	private record PropertyMatcher(String name, ValueMatcher valueMatcher) {
		public static final StreamCodec<ByteBuf, PropertyMatcher> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			PropertyMatcher::name,
			ValueMatcher.STREAM_CODEC,
			PropertyMatcher::valueMatcher,
			PropertyMatcher::new
		);
		
		public <S extends StateHolder<?, S>> boolean match(final StateDefinition<?, S> definition, final S state) {
			Property<?> property = definition.getProperty(this.name);
			return property != null && this.valueMatcher.match(state, property);
		}
		
		public Optional<String> checkState(final StateDefinition<?, ?> states) {
			Property<?> property = states.getProperty(this.name);
			return property != null ? Optional.empty() : Optional.of(this.name);
		}
	}
	
	private record RangedMatcher(Optional<String> minValue, Optional<String> maxValue) implements ValueMatcher {
		public static final Codec<RangedMatcher> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.STRING.optionalFieldOf("min").forGetter(RangedMatcher::minValue),
			Codec.STRING.optionalFieldOf("max").forGetter(RangedMatcher::maxValue)
		).apply(i, RangedMatcher::new));
		
		public static final StreamCodec<ByteBuf, RangedMatcher> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
			RangedMatcher::minValue,
			ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8),
			RangedMatcher::maxValue,
			RangedMatcher::new
		);
		
		@Override
		public <T extends Comparable<T>> boolean match(final StateHolder<?, ?> state, final Property<T> property) {
			T value = state.getValue(property);
			if (this.minValue.isPresent()) {
				Optional<T> typedMinValue = property.getValue(this.minValue.get());
				if (typedMinValue.isEmpty() || value.compareTo(typedMinValue.get()) < 0) {
					return false;
				}
			}
			
			if (this.maxValue.isPresent()) {
				Optional<T> typedMaxValue = property.getValue(this.maxValue.get());
				if (typedMaxValue.isEmpty() || value.compareTo(typedMaxValue.get()) > 0) {
					return false;
				}
			}
			
			return true;
		}
	}
	
	private interface ValueMatcher {
		Codec<ValueMatcher> CODEC = Codec.either(ExactMatcher.CODEC, RangedMatcher.CODEC).xmap(
			Either::unwrap,
			matcher -> {
				if (matcher instanceof ExactMatcher exact) {
					return Either.left(exact);
				} else if (matcher instanceof RangedMatcher ranged) {
					return Either.right(ranged);
				} else {
					throw new UnsupportedOperationException();
				}
			});
		StreamCodec<ByteBuf, ValueMatcher> STREAM_CODEC = ByteBufCodecs.either(ExactMatcher.STREAM_CODEC, RangedMatcher.STREAM_CODEC).map(
			Either::unwrap,
			matcher -> {
				if (matcher instanceof ExactMatcher exact) {
					return Either.left(exact);
				} else if (matcher instanceof RangedMatcher ranged) {
					return Either.right(ranged);
				} else {
					throw new UnsupportedOperationException();
				}
			});
		
		<T extends Comparable<T>> boolean match(StateHolder<?, ?> state, Property<T> property);
	}
}
