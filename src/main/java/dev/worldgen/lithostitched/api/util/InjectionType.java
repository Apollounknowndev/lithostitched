package dev.worldgen.lithostitched.api.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.List;
import java.util.function.Function;

public enum InjectionType implements StringRepresentable {
	PREPEND("prepend"),
	APPEND("append"),
	REPLACE("replace");
	
	public static final Codec<InjectionType> CODEC = StringRepresentable.fromEnum(InjectionType::values);
	private final String name;
	
	InjectionType(String name) {
		this.name = name;
	}
	
	public <T> T apply(T original, T injection, Function<List<T>, T> sequencer) {
		return switch (this) {
			case PREPEND -> sequencer.apply(List.of(injection, original));
			case APPEND -> sequencer.apply(List.of(original, injection));
			case REPLACE -> injection;
		};
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
}