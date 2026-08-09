package dev.worldgen.lithostitched.impl.network;

import com.mojang.serialization.DynamicOps;
import dev.worldgen.lithostitched.impl.Lithostitched;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.attribute.EnvironmentAttributeMap;

public record ApplyStructureAttributesPacket(Tag attributes) implements CustomPacketPayload {
	public static final Type<ApplyStructureAttributesPacket> TYPE = new Type<>(Lithostitched.id("apply_structure_attributes"));
	public static final StreamCodec<RegistryFriendlyByteBuf, ApplyStructureAttributesPacket> CODEC = StreamCodec.composite(
		ByteBufCodecs.TAG,
		ApplyStructureAttributesPacket::attributes,
		ApplyStructureAttributesPacket::new
	);
	
	public static ApplyStructureAttributesPacket createEmpty() {
		return new ApplyStructureAttributesPacket(new CompoundTag());
	}
	
	public static ApplyStructureAttributesPacket create(DynamicOps<Tag> ops, EnvironmentAttributeMap attributes) {
		Tag tag = EnvironmentAttributeMap.NETWORK_CODEC.encodeStart(ops, attributes).getOrThrow(s -> new IllegalArgumentException("Failed to serialize structure attributes: " + s));
		return new ApplyStructureAttributesPacket(tag);
	}
	
	public EnvironmentAttributeMap getAttributes(DynamicOps<Tag> ops) {
		var result = EnvironmentAttributeMap.NETWORK_CODEC.parse(ops, this.attributes);
		if (result.isSuccess()) return result.getOrThrow();
		Lithostitched.LOGGER.error("Couldn't parse structure attribute payload: {}", result.error().get().message());
		return EnvironmentAttributeMap.EMPTY;
	}
	
	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
