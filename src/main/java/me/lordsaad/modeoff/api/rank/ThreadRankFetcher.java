package me.lordsaad.modeoff.api.rank;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lordsaad.modeoff.ModeratorOff;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;


public class ThreadRankFetcher extends Thread {

	public ThreadRankFetcher() {
		setName("Rank Fetcher");
		start();
	}

	@Override
	public void run() {
		ModeratorOff.logger.info("<<========================================================================>>");
		ModeratorOff.logger.info("About to fetch ranks...");
		try {
			for (IRank rank : RankRegistry.INSTANCE.ranks) {
				String name = rank.getName().toLowerCase();

				ModeratorOff.logger.info("> Fetching '" + name + "' players");

				URL url = new URL("https://raw.githubusercontent.com/Demoniaque/ModeratorOff/master/ranks/" + name + ".txt");
				BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));

				String line;
				while ((line = r.readLine()) != null) {

					ModeratorOff.logger.info("    >| Looking up '" + line + "'");

					// GET PLAYER UUID FROM MOJANG
					URL mojangAPI = new URL("https://api.mojang.com/users/profiles/minecraft/" + line);
					BufferedReader mojangReader = new BufferedReader(new InputStreamReader(mojangAPI.openStream()));

					StringBuilder jsonString = new StringBuilder();
					String jsonLine;
					while ((jsonLine = mojangReader.readLine()) != null) {
						jsonString.append(jsonLine);
					}

					mojangReader.close();

					JsonElement element = new JsonParser().parse(jsonString.toString());
					if (element == null || !element.isJsonObject()) {
						ModeratorOff.logger.info("      >| Could not find uuid for " + name + ".");
						continue;
					}

					JsonObject object = element.getAsJsonObject();

					if (object.has("id") && object.get("id").isJsonPrimitive()) {
						String uuid = object.getAsJsonPrimitive("id").getAsJsonPrimitive().getAsString();
						RankRegistry.INSTANCE.rankMap.put(rank, UUID.fromString(uuid));

						ModeratorOff.logger.info("      >| Found uuid for " + name + " -> " + uuid + ". Success!");
					} else {
						ModeratorOff.logger.info("      >| Could not find uuid for " + name + ".");
					}

				}
				r.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		ModeratorOff.logger.info("<<========================================================================>>");
	}
}
