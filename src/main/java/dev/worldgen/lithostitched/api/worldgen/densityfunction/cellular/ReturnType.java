package dev.worldgen.lithostitched.api.worldgen.densityfunction.cellular;

import com.mojang.serialization.Codec;
import dev.worldgen.lithostitched.impl.worldgen.densityfunction.CellularDensityFunction;
import net.minecraft.util.StringRepresentable;

public enum ReturnType implements StringRepresentable {
	CELL_VALUE("cell_value"),
	DISTANCE_1("distance_1"),
	DISTANCE_2("distance_2"),
	DISTANCE_2_ADD("distance_2_add"),
	DISTANCE_2_SUB("distance_2_sub"),
	DISTANCE_2_MUL("distance_2_mul"),
	DISTANCE_2_DIV("distance_2_div");
	
	public static final Codec<ReturnType> CODEC = StringRepresentable.fromValues(ReturnType::values);
	private final String name;
	
	ReturnType(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
}
