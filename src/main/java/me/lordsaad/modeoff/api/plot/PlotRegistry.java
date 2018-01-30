package me.lordsaad.modeoff.api.plot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.permissions.Permission;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlotRegistry {

	public final static PlotRegistry INSTANCE = new PlotRegistry();

	public Set<Plot> plots = new HashSet<>();

	private File directory;

	private PlotRegistry() {
	}

	public int getNextAvailableID() {
		int id = 0;

		while (PlotRegistry.INSTANCE.getPlot(id) != null) {
			id++;
		}

		return id;
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

	@Nullable
	public Plot findPlot(BlockPos pos) {
		for (Plot plot : plots) {
			if (plot.getDimensions().isBlockInside(pos))
				return plot;
		}
		return null;
	}

	public void deletePlot(Plot plot) {
		plots.remove(plot);

		directory.toPath().resolve("plot_" + plot.getID() + ".json").toFile().delete();
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
		object.addProperty("modName", plot.getModName());

		JsonArray owners = new JsonArray();
		for (UUID uuid : plot.getOwners()) {
			owners.add(uuid.toString());
		}
		object.add("owners", owners);

		JsonArray perms = new JsonArray();
		for (Permission permission : plot.getPermissions()) {
			perms.add(permission.getTagID());
		}
		object.add("permissions", perms);

		JsonArray inventory = new JsonArray();
		for (ItemStack itemStack : plot.getPlayerInventory()) {
			JsonObject object1 = new JsonObject();

			ResourceLocation location = itemStack.getItem().getRegistryName();
			if (location == null) continue;

			object1.addProperty("item", location.toString());
			object1.addProperty("damage", itemStack.getItemDamage());
			object1.addProperty("count", itemStack.getCount());
			inventory.add(object1);
		}
		object.add("inventory", inventory);

		try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(directory.toPath().resolve("plot_" + plot.getID() + ".json")))) {
			Streams.write(object, writer);
		} catch (IOException e) {
			ModeratorOff.logger.error("Error saving plot", e);
		}
	}

	public void loadPlots() {
		ModeratorOff.logger.info("<<========================================================================>>");
		ModeratorOff.logger.info("> Starting plot loading.");

		plots.clear();

		if (directory == null) {
			ModeratorOff.logger.info("> Main directory does not exist!");
			return;
		}

		if (!directory.exists()) directory.mkdirs();

		int counter = 0;

		File[] fileList = directory.listFiles();
		if (fileList == null) {
			ModeratorOff.logger.info("> Cannot loop through files in directory!");
			return;
		}

		for (File file : fileList) {
			if (file == null) continue;

			if (!file.exists()) continue;

			if (!file.canRead()) {
				ModeratorOff.logger.info("    > Cannot read file '" + file.getName() + "'. Check file permissions.");
				continue;
			}

			if (!file.canWrite()) {
				ModeratorOff.logger.info("    > Cannot write file '" + file.getName() + "'. Check file permissions.");
				continue;
			}

			try {
				JsonElement element = new JsonParser().parse(new FileReader(file));

				if (element == null) {
					ModeratorOff.logger.info("    > Could not parse json of '" + file.getName() + "'. Skipping...");
					continue;
				}

				JsonObject object = element.getAsJsonObject();

				if (object.has("id") && object.get("id").isJsonPrimitive()
						&& object.has("modName") && object.get("modName").isJsonPrimitive()
						&& object.has("owners") && object.get("owners").isJsonArray()) {

					int id = object.getAsJsonPrimitive("id").getAsInt();
					String modName = object.getAsJsonPrimitive("modName").getAsString();

					HashSet<UUID> uuids = new HashSet<>();
					JsonArray owners = object.getAsJsonArray("owners");
					for (JsonElement ownerElement : owners) {
						if (!ownerElement.isJsonPrimitive()) {
							continue;
						}

						String uuid = ownerElement.getAsJsonPrimitive().getAsString();
						uuids.add(UUID.fromString(uuid));

						ModeratorOff.logger.info("    > Added owner of uuid " + uuid + " to plot " + id);
					}

					Plot plot = new Plot(id, modName, uuids);

					ModeratorOff.logger.info("    > Found plot id '" + plot.getID() + "'");

					if (object.has("permissions")) {
						JsonArray tagsArray = object.getAsJsonArray("permissions");
						for (JsonElement tagElement : tagsArray) {
							if (!tagElement.isJsonPrimitive()) {
								continue;
							}

							Permission permission = PermissionRegistry.INSTANCE.getPermission(tagElement.getAsString());

							if (permission != null) {
								plot.addPermission(permission);

								ModeratorOff.logger.info("    > Added permission " + permission.getTagID() + " to plot " + plot.getID());
							} else ModeratorOff.logger.info("Invalid permission -> " + tagElement.getAsString());

						}
					}

					if (object.has("inventory")) {
						NonNullList<ItemStack> stacks = NonNullList.create();
						JsonArray tagsArray = object.getAsJsonArray("inventory");
						for (JsonElement tagElement : tagsArray) {
							if (!tagElement.isJsonObject()) {
								continue;
							}

							JsonObject object1 = tagElement.getAsJsonObject();

							if (!object1.has("item")) {
								ModeratorOff.logger.error("Item not found in " + file.getName());
								continue;
							}


							Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(object1.getAsJsonPrimitive("item").getAsString()));
							if (item == null) {
								ModeratorOff.logger.info("Invalid item -> " + object1.getAsJsonPrimitive("item").getAsString() + " in " + file.getName());
								continue;
							}

							int damage = 0;
							if (object1.has("damage") && object1.get("damage").isJsonPrimitive() && object1.getAsJsonPrimitive("damage").isNumber()) {
								damage = object1.getAsJsonPrimitive("damage").getAsInt();
							}

							int count = 0;
							if (object1.has("count") && object1.get("count").isJsonPrimitive() && object1.getAsJsonPrimitive("count").isNumber()) {
								count = object1.getAsJsonPrimitive("count").getAsInt();
							}

							ItemStack stack = new ItemStack(item, count, damage);

							ModeratorOff.logger.info("    > Added itemstack " + stack.getItem().getUnlocalizedName() + "x" + stack.getCount() + " to plot " + plot.getID());
							stacks.add(stack);
						}

						plot.getPlayerInventory().addAll(stacks);
					}

					plots.add(plot);
					counter++;

					ModeratorOff.logger.info("    > Successfully registered plot id '" + plot.getID() + "'");
				}

			} catch (FileNotFoundException e) {
				ModeratorOff.logger.info("    > Error reading json of '" + file.getName() + "'. -> " + e.getCause());
				e.printStackTrace();
			}
		}
		ModeratorOff.logger.info("> Successfully registered " + counter + " plots.");
		ModeratorOff.logger.info("<<========================================================================>>");
	}

	public void setDirectory(File directory) {
		this.directory = directory;
	}
}
