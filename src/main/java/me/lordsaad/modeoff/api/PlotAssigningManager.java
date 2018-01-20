package me.lordsaad.modeoff.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teamwizardry.librarianlib.features.kotlin.JsonMaker;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.server.ServerProxy;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class PlotAssigningManager {

	public static PlotAssigningManager INSTANCE = new PlotAssigningManager();

	private File plotFile;

	private PlotAssigningManager() {
		File directory = ServerProxy.directory;
		if (!directory.exists()) {
			ModeratorOff.logger.info(directory.getName() + " directory not found. Creating directory...");
			if (!directory.mkdirs()) {
				ModeratorOff.logger.fatal("SOMETHING WENT WRONG! Could not create config directory " + directory.getName());
				return;
			}
			ModeratorOff.logger.info(directory.getName() + " directory has been created successfully!");
		}

		File plotFile = new File(directory, "registered_plots.json");
		try {
			if (!plotFile.exists()) {
				ModeratorOff.logger.info(plotFile.getName() + " file not found. Creating file...");
				if (!plotFile.createNewFile()) {
					ModeratorOff.logger.fatal("SOMETHING WENT WRONG! Could not create config file " + plotFile.getName());
					return;
				}

				JsonObject obj = new JsonObject();
				obj.add("plots", new JsonArray());

				FileWriter writer = new FileWriter(plotFile);
				writer.write(JsonMaker.serialize(obj));
				writer.flush();
				writer.close();
				ModeratorOff.logger.info(plotFile.getName() + " file has been created successfully!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.plotFile = plotFile;
	}

	public boolean isUUIDRegistered(UUID uuid) {
		JsonElement json;
		try {
			json = new JsonParser().parse(new FileReader(plotFile));
			if (json.isJsonObject() && json.getAsJsonObject().has("plots") && json.getAsJsonObject().get("plots").isJsonArray()) {
				JsonArray array = json.getAsJsonObject().getAsJsonArray("plots");
				for (JsonElement element : array) {
					if (element.isJsonObject() && element.getAsJsonObject().has("uuid")) {
						UUID fetched = UUID.fromString(element.getAsJsonObject().getAsJsonPrimitive("uuid").getAsString());
						if (uuid.equals(fetched)) return true;
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	public int getNextAvailableID() {
		JsonElement json;
		try {
			json = new JsonParser().parse(new FileReader(plotFile));
			if (json.isJsonObject() && json.getAsJsonObject().has("plots") && json.getAsJsonObject().get("plots").isJsonArray()) {
				JsonArray array = json.getAsJsonObject().getAsJsonArray("plots");
				if (array.size() == 0) return 0;
				return array.size();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public boolean saveUUIDToPlot(UUID uuid, int plotID) {
		if (isUUIDRegistered(uuid)) return false;

		JsonElement json;
		try {
			json = new JsonParser().parse(new FileReader(plotFile));
			if (json.isJsonObject() && json.getAsJsonObject().has("plots") && json.getAsJsonObject().get("plots").isJsonArray()) {
				JsonArray array = json.getAsJsonObject().getAsJsonArray("plots");

				ArrayList<JsonObject> plots = new ArrayList<>();
				for (JsonElement element : array)
					if (element.isJsonObject()) plots.add(element.getAsJsonObject());

				JsonObject newPlot = new JsonObject();
				newPlot.addProperty("uuid", uuid.toString());
				newPlot.addProperty("id", plotID);

				JsonArray newArray = new JsonArray();
				for (JsonElement element : plots) {
					newArray.add(element);
				}
				newArray.add(newPlot);

				JsonObject obj = new JsonObject();
				obj.add("plots", newArray);

				try {
					FileWriter writer = new FileWriter(plotFile);
					writer.write(JsonMaker.serialize(obj));
					writer.flush();
					writer.close();
					ModeratorOff.logger.info("registered plot " + plotID + " to uuid '" + uuid.toString() + "' successfully!");

					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return false;
	}

	public int getPlotForUUID(UUID uuid) {
		JsonElement json;
		try {
			json = new JsonParser().parse(new FileReader(plotFile));
			if (json.isJsonObject() && json.getAsJsonObject().has("plots") && json.getAsJsonObject().get("plots").isJsonArray()) {
				JsonArray array = json.getAsJsonObject().getAsJsonArray("plots");
				for (JsonElement element : array) {
					if (element.isJsonObject() && element.getAsJsonObject().has("id") && element.getAsJsonObject().has("uuid")) {
						if (UUID.fromString(element.getAsJsonObject().getAsJsonPrimitive("uuid").getAsString()).equals(uuid))
							return element.getAsJsonObject().getAsJsonPrimitive("id").getAsInt();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return -1;
	}

	@Nullable
	public UUID getUUIDForPlot(int plotId) {
		JsonElement json;
		try {
			json = new JsonParser().parse(new FileReader(plotFile));
			if (json.isJsonObject() && json.getAsJsonObject().has("plots") && json.getAsJsonObject().get("plots").isJsonArray()) {
				JsonArray array = json.getAsJsonObject().getAsJsonArray("plots");
				for (JsonElement element : array) {
					if (element.isJsonObject() && element.getAsJsonObject().has("id") && element.getAsJsonObject().has("uuid")) {
						if (element.getAsJsonObject().getAsJsonPrimitive("id").getAsInt() == plotId)
							return UUID.fromString(element.getAsJsonObject().getAsJsonPrimitive("uuid").getAsString());
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
