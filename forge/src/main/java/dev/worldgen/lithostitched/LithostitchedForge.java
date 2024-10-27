package dev.worldgen.lithostitched;

import dev.worldgen.lithostitched.config.ConfigHandler;
import dev.worldgen.lithostitched.registry.LithostitchedBuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.nio.file.Path;

/**
 * Mod class for Lithostitched on Forge.
 */
@Mod(LithostitchedCommon.MOD_ID)
public final class LithostitchedForge {

	public LithostitchedForge() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		LithostitchedBuiltInRegistries.init(bus);

		bus.addListener(this::registerEnabledPacks);
	}

	private void registerEnabledPacks(final AddPackFindersEvent event) {
		if (event.getPackType() == PackType.SERVER_DATA && ConfigHandler.getConfig().breaksSeedParity()) {
			Path resourcePath = ModList.get().getModFileById("lithostitched").getFile().findResource("resourcepacks/breaks_seed_parity");
			Pack dataPack = Pack.readMetaAndCreate("lithostitched/breaks_seed_parity", Component.literal("Lithostitched extras"), false, string -> new PathPackResources(resourcePath.getFileName().toString(), resourcePath, false), PackType.SERVER_DATA, Pack.Position.TOP, PackSource.BUILT_IN);
			event.addRepositorySource((packConsumer) -> packConsumer.accept(dataPack));
		}
	}

}
