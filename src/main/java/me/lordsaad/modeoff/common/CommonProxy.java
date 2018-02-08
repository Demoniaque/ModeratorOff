package me.lordsaad.modeoff.common;

import com.google.common.base.Charsets;
import com.google.common.collect.HashBiMap;
import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lordsaad.modeoff.ModItems;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.capability.ModoffCapabilityProvider;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.IRank;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.client.gui.GuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class CommonProxy {

	@Nullable
	public static File directory = null;

	public static HashBiMap<String, UUID> playerUUIDMap = HashBiMap.create();
	public static Set<Set<UUID>> teams = new HashSet<>();

	public void preInit(FMLPreInitializationEvent event) {
		ModItems.init();
		ModoffCapabilityProvider.init();

		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(RankRegistry.INSTANCE);

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
	}

	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(ModeratorOff.instance, new GuiHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {
		PlotRegistry.INSTANCE.setDirectory(directory);
		PlotRegistry.INSTANCE.loadPlots();
		fetchRanks();
	}

	public static void fetchRanks() {
		ModeratorOff.logger.info("<<========================================================================>>");
		ModeratorOff.logger.info("About to fetch ranks...");
		try {
			for (IRank rank : RankRegistry.INSTANCE.ranks.inverse().keySet()) {
				String name = rank.getName().toLowerCase();

				ModeratorOff.logger.info("> Fetching '" + name + "' players");

				String rankJson = Resources.asCharSource(new URL("https://raw.githubusercontent.com/Demoniaque/ModeratorOff/master/ranks/" + name + ".json"), Charsets.UTF_8).read();

				JsonElement jsonElement = new JsonParser().parse(rankJson);
				if (jsonElement == null || !jsonElement.isJsonArray()) {
					ModeratorOff.logger.info("  > Error reading json " + name);
					return;
				}

				JsonArray array = jsonElement.getAsJsonArray();

				for (JsonElement element : array) {
					if (element.isJsonObject()) {
						JsonObject object = element.getAsJsonObject();

						String playerName = object.getAsJsonPrimitive("name").getAsString();
						UUID uuid = UUID.fromString(object.getAsJsonPrimitive("uuid").getAsString());
						ModeratorOff.logger.info("    > Found player -> " + playerName + " - " + uuid.toString());

						RankRegistry.INSTANCE.rankMap.put(rank, uuid);
						playerUUIDMap.put(playerName, uuid);
					} else if (element.isJsonArray()) {

						JsonArray team = element.getAsJsonArray();
						Set<UUID> teamSet = new HashSet<>();

						for (JsonElement teamElement : team) {
							if (teamElement.isJsonObject()) {
								JsonObject object = element.getAsJsonObject();

								String playerName = object.getAsJsonPrimitive("name").getAsString();
								UUID uuid = UUID.fromString(object.getAsJsonPrimitive("uuid").getAsString());
								ModeratorOff.logger.info("    > Found player -> " + playerName + " - " + uuid.toString());

								RankRegistry.INSTANCE.rankMap.put(rank, uuid);
								playerUUIDMap.put(playerName, uuid);
								CommonProxy.teams.add(teamSet);
							}
						}
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		ModeratorOff.logger.info("<<========================================================================>>");
	}
}
