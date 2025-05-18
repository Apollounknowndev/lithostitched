package dev.worldgen.lithostitched.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

// __X fields are for comments. It's a disgusting hack, but I don't particularly care
public record ConfigCodec(String commentA, String commentB, String commentC, String commentD, boolean breaksSeedParity, boolean logDebugMessages) {
    private static final String COMMENT_A = "If disabled, some mod compat features will be turned off to prioritize parity with vanilla seeds.";
    private static final String COMMENT_B = "The following features will break if disabled:";
    private static final String COMMENT_C = "- Custom wood type shipwrecks";
    private static final String COMMENT_D = "- Structure optimizations";

    public static final Codec<ConfigCodec> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("__A").orElse(COMMENT_A).forGetter(ConfigCodec::commentA),
            Codec.STRING.fieldOf("__B").orElse(COMMENT_B).forGetter(ConfigCodec::commentB),
            Codec.STRING.fieldOf("__C").orElse(COMMENT_C).forGetter(ConfigCodec::commentC),
            Codec.STRING.fieldOf("__D").orElse(COMMENT_D).forGetter(ConfigCodec::commentD),
            Codec.BOOL.fieldOf("breaks_seed_parity").orElse(true).forGetter(ConfigCodec::breaksSeedParity),
            Codec.BOOL.fieldOf("log_debug_messages").orElse(false).forGetter(ConfigCodec::logDebugMessages)
    ).apply(instance, ConfigCodec::new));
    public static final ConfigCodec DEFAULT = new ConfigCodec(COMMENT_A, COMMENT_B, COMMENT_C, COMMENT_D, true, false);
}
