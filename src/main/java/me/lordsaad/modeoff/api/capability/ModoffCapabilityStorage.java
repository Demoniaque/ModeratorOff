package me.lordsaad.modeoff.api.capability;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

/**
 * Created by Saad on 8/16/2016.
 */
public class ModoffCapabilityStorage implements IStorage<IModoffCapability> {

	public static final ModoffCapabilityStorage INSTANCE = new ModoffCapabilityStorage();

	@Override
	public NBTBase writeNBT(Capability<IModoffCapability> capability, IModoffCapability instance, EnumFacing side) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setDouble("rank_id", instance.getRankID());
		return nbt;
	}

	@Override
	public void readNBT(Capability<IModoffCapability> capability, IModoffCapability instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound tag = (NBTTagCompound) nbt;
		instance.setRank(tag.getInteger("rank_id"));
	}
}
