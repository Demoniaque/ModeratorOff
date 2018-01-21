package me.lordsaad.modeoff.api.plot;

import com.teamwizardry.librarianlib.features.math.Vec2d;
import me.lordsaad.modeoff.api.ConfigValues;
import me.lordsaad.modeoff.api.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class Plot implements INBTSerializable<NBTTagCompound> {

	private int id;
	private HashSet<UUID> owners;
	private PlotDimensions dimensions;

	public Plot(int id, @NotNull HashSet<UUID> owners) {
		this.id = id;
		this.owners = owners;

		dimensions = new PlotDimensions(id);
	}

	public Plot() {
		owners = new HashSet<>();
	}

	public void teleportToPlot(EntityPlayer player) {
		if (player == null) return;

		if (player.world.provider.getDimension() != ConfigValues.plotWorldDimensionID)
			player.changeDimension(ConfigValues.plotWorldDimensionID);

		Vec2d pos = getPlotPos();

		player.setPositionAndUpdate(pos.getX(), ConfigValues.y, pos.getY());
	}

	public Vec2d getPlotPos() {
		Vec2d center = Utils.spiralLocFromID(getID(), Vec2d.ZERO);

		center = center.mul(ConfigValues.plotSize + ConfigValues.plotMarginWidth);

		center = center.add(ConfigValues.x, ConfigValues.z).add(0.5, 0.5);
		return center;
	}

	@NotNull
	public Collection<UUID> getOwners() {
		return owners;
	}

	public int getID() {
		return id;
	}

	@NotNull
	public PlotDimensions getDimensions() {
		return dimensions;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("id", id);

		NBTTagList owners = new NBTTagList();
		for (UUID uuid : this.owners) {
			owners.appendTag(new NBTTagString(uuid.toString()));
		}

		compound.setTag("owners", owners);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("id")) id = nbt.getInteger("id");

		if (nbt.hasKey("owners")) {

			NBTTagList list = nbt.getTagList("owners", Constants.NBT.TAG_STRING);

			for (int i = 0; i < list.tagCount(); i++) {
				String owner = list.getStringTagAt(i);
				owners.add(UUID.fromString(owner));
			}
		}
		dimensions = new PlotDimensions(getID());
	}

	public class PlotDimensions {
		private final BlockPos corner1, corner2;

		public PlotDimensions(int plotID) {
			Vec2d center = Utils.spiralLocFromID(plotID, Vec2d.ZERO);

			center = center.mul(ConfigValues.plotSize + ConfigValues.plotMarginWidth);

			center = center.add(ConfigValues.x, ConfigValues.z).add(0.5, 0.5);

			corner1 = new BlockPos(center.getX() - (ConfigValues.plotSize / 2), ConfigValues.y, center.getY() - (ConfigValues.plotSize / 2));
			corner2 = new BlockPos(center.getX() + (ConfigValues.plotSize / 2), ConfigValues.y, center.getY() + (ConfigValues.plotSize / 2));
		}

		public BlockPos getCorner1() {
			return corner1;
		}

		public BlockPos getCorner2() {
			return corner2;
		}
	}
}
