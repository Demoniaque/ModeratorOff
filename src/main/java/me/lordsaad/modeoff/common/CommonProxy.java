package me.lordsaad.modeoff.common;

import com.teamwizardry.librarianlib.features.config.EasyConfigHandler;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.ThreadRankFetcher;
import me.lordsaad.modeoff.client.gui.GuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.io.File;

/**
 * Created by LordSaad.
 */
public class CommonProxy {

	public static File directory;

	public void preInit(FMLPreInitializationEvent event) {
		EasyConfigHandler.init();

		File configFolder = new File(event.getModConfigurationDirectory(), "/plots/");

		if (!configFolder.exists()) {
			ModeratorOff.logger.info(configFolder.getName() + " not found. Creating directory...");
			if (!configFolder.mkdirs()) {
				ModeratorOff.logger.error("SOMETHING WENT WRONG! Could not create config directory " + configFolder.getName());
				return;
			}
			ModeratorOff.logger.info(configFolder.getName() + " has been created successfully!");
		}

		directory = configFolder;

		PlotRegistry.INSTANCE.setDirectory(directory);
		PlotRegistry.INSTANCE.loadPlots();

		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(ModeratorOff.instance, new GuiHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {
		new ThreadRankFetcher();
	}
}
