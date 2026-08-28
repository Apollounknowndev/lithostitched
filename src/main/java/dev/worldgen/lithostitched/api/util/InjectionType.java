package dev.worldgen.lithostitched.api.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum InjectionType implements StringRepresentable {
	PREPEND("prepend"),
	APPEND("append"),
	REPLACE("replace");
	
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