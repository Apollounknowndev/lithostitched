package dev.worldgen.lithostitched.api.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum InjectionType implements StringRepresentable {
	PREPEND("prepend"),
	APPEND("append");
	
	public static final Codec<InjectionType> CODEC = StringRepresentable.fromEnum(InjectionType::values);
	private final String name;
	
	InjectionType(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
}