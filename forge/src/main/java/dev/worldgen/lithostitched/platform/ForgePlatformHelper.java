package dev.worldgen.lithostitched.platform;

import dev.worldgen.lithostitched.platform.services.IPlatformHelper;
import net.minecraftforge.fml.ModList;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }
}
