package me.lordsaad.modeoff.api.plot;

import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.teamwizardry.librarianlib.features.math.Vec2d;
import me.lordsaad.modeoff.api.ConfigValues;
import me.lordsaad.modeoff.api.Utils;
import me.lordsaad.modeoff.api.permissions.IPermissionHolder;
import me.lordsaad.modeoff.api.permissions.Permission;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Plot implements INBTSerializable<NBTTagCompound>, IPermissionHolder {

	private int id;

	private String modName;

	private Set<UUID> owners;

	private PlotDimensions dimensions;

	private Set<Permission> permissions;

	private NonNullList<ItemStack> playerInventory;

	private Plot() {
		this(null, new HashSet<>());
	}

	public Plot(String modName, UUID ownerFirst, UUID... owners) {
		this(modName, Sets.newHashSet(ObjectArrays.concat(ownerFirst, owners)));
	}

	public Plot(String modName, Set<UUID> owners) {
		this(PlotRegistry.INSTANCE.getNextAvailableID(), modName, owners);
	}

	public Plot(int id, String modName, Set<UUID> owners) {
		this(id, modName, owners, new PlotDimensions(id), new HashSet<>(), NonNullList.create());
	}

	private Plot(int id, String modName, Set<UUID> owners, PlotDimensions dimensions, Set<Permission> permissions, NonNullList<ItemStack> playerInventory) {
		this.id = id;
		this.modName = modName;
		this.owners = owners;
		this.dimensions = dimensions;
		this.permissions = permissions;
		this.playerInventory = playerInventory;
	}

	public void teleport(EntityPlayer player) {
		if (player.world.provider.getDimension() != ConfigValues.plotWorldDimensionID) {
			player.changeDimension(ConfigValues.plotWorldDimensionID);
		}

		Vec2d pos = getPlotPos();

		player.setPositionAndUpdate(pos.getX(), ConfigValues.y, pos.getY());
	}

	public void onEnter(EntityPlayer player) {
		for (ItemStack stack : playerInventory) {
			ItemHandlerHelper.giveItemToPlayer(player, stack);
		}
	}

	public void onLeave(EntityPlayer player) {

	}

	public Vec2d getPlotPos() {
		Vec2d center = Utils.spiralLocFromID(getID(), Vec2d.ZERO);

		center = center.mul(ConfigValues.plotSize + ConfigValues.plotMarginWidth);

		center = center.add(ConfigValues.x, ConfigValues.z).add(0.5, 0.5);
		return center;
	}

	public String getModName() {
		return modName;
	}

	public void setModName(String modName) {
		this.modName = modName;
	}

	public Set<UUID> getOwners() {
		return owners;
	}

	public int getID() {
		return id;
	}

	public PlotDimensions getDimensions() {
		return dimensions;
	}

	@Override
	public Set<Permission> getPermissions() {
		return permissions;
	}

	@Override
	public void addPermission(Permission permission) {
		permissions.add(permission);
	}

	@Override
	public void removePermission(Permission permission) {
		permissions.remove(permission);
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("id", id);

		compound.setString("mod_name", modName);

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

		NBTTagList items = new NBTTagList();
		for (ItemStack stack : playerInventory) {
			items.appendTag(stack.serializeNBT());
		}
		compound.setTag("playerInventory", items);
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("id")) {
			id = nbt.getInteger("id");
		}
		if (nbt.hasKey("mod_name")) {
			modName = nbt.getString("mod_name");
		}

		if (nbt.hasKey("owners")) {
			owners.clear();
			NBTTagList list = nbt.getTagList("owners", Constants.NBT.TAG_STRING);
			for (int i = 0; i < list.tagCount(); i++) {
				String owner = list.getStringTagAt(i);
				owners.add(UUID.fromString(owner));
			}
		}

		if (nbt.hasKey("permissions")) {
			permissions.clear();
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

		if (nbt.hasKey("playerInventory")) {
			playerInventory.clear();
			NBTTagList items = nbt.getTagList("playerInventory", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < items.tagCount(); i++) {
				playerInventory.add(new ItemStack(items.getCompoundTagAt(i)));
			}
		}
	}

	public static Plot deserialize(NBTTagCompound compound) {
		Plot plot = new Plot();
		plot.deserializeNBT(compound);
		return plot;
	}

}
