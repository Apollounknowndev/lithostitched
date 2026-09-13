package dev.worldgen.lithostitched.mixin.common;

import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.lang.classfile.Opcode;
import java.util.Optional;

@Mixin(NoiseGeneratorSettings.class)
public interface NoiseGeneratorSettingsAccessor {
    @Accessor("noiseRouter")
    @Mutable
    void setNoiseRouter(NoiseRouter noiseRouter);
    
    @Accessor("aquifers")
    @Mutable
    void setAquifers(Optional<Aquifer.Config> aquifers);
}
