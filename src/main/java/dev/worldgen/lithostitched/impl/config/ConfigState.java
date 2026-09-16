package dev.worldgen.lithostitched.impl.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.worldgen.apollib.codec.ApollibCodecs;
import dev.worldgen.apollib.config.ApollibCopyable;

public class ConfigState implements ApollibCopyable<ConfigState> {
	public static final Codec<ConfigState> CODEC = RecordCodecBuilder.create(i -> i.group(
		ApollibCodecs.optionalCommented(Codec.BOOL, true, "breaks_seed_parity", "If disabled, some features will be turned off to prioritize parity with vanilla seeds. This includes custom wood type shipwrecks and some optimizations to Jigsaw structures.").forGetter(s -> s.breaksSeedParity),
		Codec.BOOL.fieldOf("log_debug_messages").orElse(false).forGetter(s -> s.logDebugMessages),
		Codec.BOOL.fieldOf("enable_debug_suite").orElse(false).forGetter(s -> s.enableDebugSuite)
	).apply(i, ConfigState::new));
	public static final ConfigState DEFAULT = new ConfigState(true, false, false);
	
	public boolean breaksSeedParity;
	public boolean logDebugMessages;
	public boolean enableDebugSuite;
	
	public ConfigState(boolean breaksSeedParity, boolean logDebugMessages, boolean enableDebugSuite) {
		this.breaksSeedParity = breaksSeedParity;
		this.logDebugMessages = logDebugMessages;
		this.enableDebugSuite = enableDebugSuite;
	}
	
	@Override
	public ConfigState copy() {
		return new ConfigState(this.breaksSeedParity, this.logDebugMessages, this.enableDebugSuite);
	}
}
