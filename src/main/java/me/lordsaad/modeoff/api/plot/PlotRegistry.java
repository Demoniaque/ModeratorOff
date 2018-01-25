package me.lordsaad.modeoff.api.plot;

import com.google.gson.*;
import me.lordsaad.modeoff.ModeratorOff;

import javax.annotation.Nullable;
import java.io.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class PlotRegistry {

	public final static PlotRegistry INSTANCE = new PlotRegistry();

	public HashSet<Plot> plots = new HashSet<>();

	private File directory;

	private PlotRegistry() {
	}

	public int getNextAvailableID() {
		int largestID = -1;
		for (Plot plot : plots) {
			if (largestID < plot.getID()) largestID = plot.getID();
		}

		return ++largestID;
	}

	public Plot registerPlot(Plot plot) {
		plots.add(plot);
		savePlot(plot.getID());

		return plot;
	}

	@Nullable
	public Plot getPlot(int id) {
		for (Plot plot : plots) if (plot.getID() == id) return plot;
		return null;
	}

	@Nullable
	public Plot getPlot(UUID uuid) {
		for (Plot plot : plots)
			for (UUID owner : plot.getOwners()) {
				if (owner.equals(uuid)) return plot;
			}
		return null;
	}

	public boolean isUUIDRegistered(UUID uuid) {
		for (Plot plot : plots) {
			for (UUID owner : plot.getOwners()) {
				if (owner.equals(uuid)) {
					return true;
				}
			}
		}
		return false;
	}

	public void savePlots() {
		for (Plot plot : plots) {
			savePlot(plot.getID());
		}
	}

	public void savePlot(int plotID) {
		Plot plot = getPlot(plotID);
		if (plot == null) return;

		JsonObject object = new JsonObject();

		object.addProperty("id", plot.getID());

		JsonArray owners = new JsonArray();
		for (UUID uuid : plot.getOwners()) {
			owners.add(uuid.toString());
		}
		object.add("owners", owners);

		JsonArray tags = new JsonArray();
		for (String tag : plot.getTags()) {
			tags.add(tag);
		}
		object.add("tags", tags);

		File file = new File(directory, "plot_" + plot.getID() + ".json");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ignored) {
			}
		}

		try {
			FileWriter writer = new FileWriter(file);
			new Gson().toJson(object, writer);
			writer.flush();
			ModeratorOff.logger.info("    > Saved plot id `" + plot.getID() + "` to file `" + file.getName() + "`");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadPlots() {
		ModeratorOff.logger.info("<<========================================================================>>");
		ModeratorOff.logger.info("> Starting plot loading.");

		if (directory == null) {
			ModeratorOff.logger.info("> Main directory does not exist!");
			return;
		}

		int count = 0;
		for (File file : Objects.requireNonNull(directory.listFiles())) {
			if (file == null) continue;

			if (!file.exists()) continue;

			if (!file.canRead()) {
				ModeratorOff.logger.info("    > Cannot read file `" + file.getName() + "`. Check file permissions.");
				continue;
			}

			if (!file.canWrite()) {
				ModeratorOff.logger.info("    > Cannot write file `" + file.getName() + "`. Check file permissions.");
				continue;
			}

			try {
				JsonElement element = new JsonParser().parse(new FileReader(file));

				if (element == null) {
					ModeratorOff.logger.info("    > Could not parse json of `" + file.getName() + "`. Skipping...");
					continue;
				}

				JsonObject object = element.getAsJsonObject();

				if (object.has("id") && object.get("id").isJsonPrimitive()
						&& object.has("owners") && object.get("owners").isJsonArray()) {

					int id = object.getAsJsonPrimitive("id").getAsInt();

					HashSet<UUID> uuids = new HashSet<>();
					JsonArray owners = object.getAsJsonArray("owners");
					for (JsonElement ownerElement : owners) {
						if (!ownerElement.isJsonPrimitive()) {
							continue;
						}

						String uuid = ownerElement.getAsJsonPrimitive().getAsString();
						uuids.add(UUID.fromString(uuid));
					}

					Plot plot = new Plot(id, uuids);

					if (object.has("tags")) {
						JsonArray tagsArray = object.getAsJsonArray("tags");
						for (JsonElement tagElement : tagsArray) {
							if (!tagElement.isJsonPrimitive()) {
								plot.addTag(tagElement.getAsString());
							}
						}
					}

					plots.add(plot);
					count++;

					ModeratorOff.logger.info("    > Successfully registered plot id `" + plot.getID() + "`");
				}

			} catch (FileNotFoundException e) {
				ModeratorOff.logger.info("    > Error reading json of `" + file.getName() + "`. -> " + e.getCause());
				e.printStackTrace();
			}
		}
		ModeratorOff.logger.info("> Successfully registered " + count + " plots.");
		ModeratorOff.logger.info("<<========================================================================>>");
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}
}
