package me.lordsaad.modeoff.api.plot;

import com.teamwizardry.librarianlib.features.math.Vec2d;
import me.lordsaad.modeoff.api.ConfigValues;
import me.lordsaad.modeoff.api.Utils;
import me.lordsaad.modeoff.api.permissions.IPermissionHolder;
import me.lordsaad.modeoff.api.permissions.Permission;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Plot implements INBTSerializable<NBTTagCompound>, IPermissionHolder {

	private int id;
	private Set<UUID> owners;
	private PlotDimensions dimensions;

	private Set<Permission> permissions = new HashSet<>();

	public Plot(int id, @NotNull Set<UUID> owners) {
		this.id = id;
		this.owners = owners;

		dimensions = new PlotDimensions(id);
	}

	public Plot(@NotNull Set<UUID> owners) {
		this.id = PlotRegistry.INSTANCE.getNextAvailableID();
		this.owners = owners;

		dimensions = new PlotDimensions(id);
	}

	public Plot(UUID... owner) {
		this.id = PlotRegistry.INSTANCE.getNextAvailableID();
		this.owners = new HashSet<>();
		owners.addAll(Arrays.asList(owner));

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
	public Set<Permission> getPermissions() {
		return permissions;
	}

	@Override
	public void addPermission(Permission permission) {
		getPermissions().add(permission);
	}

	@Override
	public void removePermission(Permission permission) {
		permissions.remove(permission);
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

		NBTTagList perms = new NBTTagList();
		for (Permission permission : this.permissions) {
			perms.appendTag(permission.serializeNBT());
		}
		compound.setTag("permissions", perms);

		compound.setTag("dimensions", dimensions.serializeNBT());

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

		if (nbt.hasKey("permissions")) {
			permissions = new HashSet<>();

			NBTTagList list = nbt.getTagList("permissions", Constants.NBT.TAG_STRING);

			for (int i = 0; i < list.tagCount(); i++) {
				String tag = list.getStringTagAt(i);
				permissions.add(PermissionRegistry.INSTANCE.getPermission(tag));
			}
		}

		if (nbt.hasKey("dimensions")) {
			NBTTagCompound compound = nbt.getCompoundTag("dimensions");
			dimensions = new PlotDimensions();
			dimensions.deserializeNBT(compound);

		}
	}

	public void save() {
		PlotRegistry.INSTANCE.savePlot(getID());
	}

	public class PlotDimensions implements INBTSerializable<NBTTagCompound> {
		private BlockPos corner1, corner2;

		PlotDimensions(int plotID) {
			Vec2d center = Utils.spiralLocFromID(plotID, Vec2d.ZERO);

			center = center.mul(ConfigValues.plotSize + ConfigValues.plotMarginWidth);

			center = center.add(ConfigValues.x, ConfigValues.z).add(0.5, 0.5);

			corner1 = new BlockPos(center.getX() - (ConfigValues.plotSize / 2), ConfigValues.y, center.getY() - (ConfigValues.plotSize / 2));
			corner2 = new BlockPos(center.getX() + (ConfigValues.plotSize / 2), ConfigValues.y, center.getY() + (ConfigValues.plotSize / 2));
		}

		public PlotDimensions() {
		}

		public BlockPos getCorner1() {
			return corner1;
		}

		public BlockPos getCorner2() {
			return corner2;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound compound = new NBTTagCompound();
			compound.setLong("corner1", getCorner1().toLong());
			compound.setLong("corner2", getCorner2().toLong());
			return compound;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			if (nbt.hasKey("corner1") && nbt.hasKey("corner2")) {
				corner1 = BlockPos.fromLong(nbt.getLong("corner1"));
				corner2 = BlockPos.fromLong(nbt.getLong("corner2"));
			}
		}

		public Iterable<BlockPos.MutableBlockPos> getAllBlocks() {
			return BlockPos.getAllInBoxMutable(
					new BlockPos(getCorner1().getX(), 0, getCorner1().getZ()),
					new BlockPos(getCorner2().getX(), 255, getCorner2().getZ()));
		}

		public boolean isBlockInside(BlockPos pos) {
			int xMin = Math.min(getCorner1().getX(), getCorner2().getX());
			int xMax = Math.max(getCorner1().getX(), getCorner2().getX());
			int zMin = Math.min(getCorner1().getZ(), getCorner2().getZ());
			int zMax = Math.max(getCorner1().getZ(), getCorner2().getZ());

			return pos.getX() < xMin
					|| pos.getX() > xMax
					|| pos.getZ() < zMin
					|| pos.getZ() > zMax;
		}
	}
}
