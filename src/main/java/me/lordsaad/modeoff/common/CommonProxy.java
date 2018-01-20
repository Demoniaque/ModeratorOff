package me.lordsaad.modeoff.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teamwizardry.librarianlib.features.config.EasyConfigHandler;
import com.teamwizardry.librarianlib.features.kotlin.JsonMaker;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.PlotRegistry;
import me.lordsaad.modeoff.api.RankManager;
import me.lordsaad.modeoff.client.gui.GuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class CommonProxy {

	public static File directory;
	public static Set<UUID> teamMembers = new HashSet<>();
	public static Set<UUID> contestants = new HashSet<>();

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
		//initRanks(directory);
	}

	private void initRanks(File directory) {
		File ranksFolder = new File(directory, "ranks");
		if (!ranksFolder.exists()) {
			ModeratorOff.logger.info(ranksFolder.getName() + " not found. Creating directory...");
			if (!ranksFolder.mkdirs()) {
				ModeratorOff.logger.error("SOMETHING WENT WRONG! Could not create config directory " + ranksFolder.getName());
				return;
			}
			ModeratorOff.logger.info(ranksFolder.getName() + " has been created successfully!");
		}
		RankManager.INSTANCE.directory = ranksFolder;

		File rankConfig = new File(ranksFolder, "ranks_config");
		try {
			if (!rankConfig.exists()) {
				ModeratorOff.logger.info(rankConfig.getName() + " file not found. Creating file...");
				if (!rankConfig.createNewFile()) {
					ModeratorOff.logger.fatal("SOMETHING WENT WRONG! Could not create config file " + rankConfig.getName());
					return;
				}

				JsonObject base = new JsonObject();
				JsonArray array = new JsonArray();

				JsonObject team = new JsonObject();
				team.addProperty("name", "Organizer");
				team.addProperty("color", Color.YELLOW.getRGB());
				team.addProperty("gm1", true);
				team.addProperty("claimable_plots", -1);
				team.addProperty("manage_others", true);

				JsonArray teamArray = new JsonArray();
				teamArray.add(new JsonPrimitive("LordSaad"));
				teamArray.add(new JsonPrimitive("Eladkay"));
				teamArray.add(new JsonPrimitive("escapee"));
				teamArray.add(new JsonPrimitive("prospector"));
				team.add("players", teamArray);

				array.add(team);

				JsonObject participant = new JsonObject();
				participant.addProperty("name", "Participant");
				participant.addProperty("color", Color.CYAN.getRGB());
				participant.addProperty("gm1", false);
				participant.addProperty("claimable_plots", 1);
				participant.addProperty("manage_others", false);
				array.add(participant);

				base.add("ranks", array);

				FileWriter writer = new FileWriter(rankConfig);
				writer.write(JsonMaker.serialize(base));
				writer.flush();
				writer.close();
				ModeratorOff.logger.info(rankConfig.getName() + " file has been created successfully!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		RankManager.INSTANCE.config = rankConfig;
	}
}
