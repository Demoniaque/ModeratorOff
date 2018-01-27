package me.lordsaad.modeoff.api.capability;

import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.Constants;

/**
 * Created by Saad on 8/16/2016.
 */
public class ModoffCapabilityStorage implements IStorage<IModoffCapability> {

	public static final ModoffCapabilityStorage INSTANCE = new ModoffCapabilityStorage();

	@Override
	public NBTBase writeNBT(Capability<IModoffCapability> capability, IModoffCapability instance, EnumFacing side) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("rank_id", instance.getRankID());
		Plot plot = instance.getEnclosingPlot();
		if (plot != null) {
			nbt.setInteger("enclosing_plot_id", plot.getID());	
		}
		return nbt;
	}

	@Override
	public void readNBT(Capability<IModoffCapability> capability, IModoffCapability instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound tag = (NBTTagCompound) nbt;
		instance.setRank(tag.getInteger("rank_id"));
		if (tag.hasKey("enclosing_plot_id", Constants.NBT.TAG_INT)) {
			instance.setEnclosingPlot(PlotRegistry.INSTANCE.getPlot(tag.getInteger("enclosing_plot_id")));
		} else {
			instance.setEnclosingPlot(null);
		}
	}
}
