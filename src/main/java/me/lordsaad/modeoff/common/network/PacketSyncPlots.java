package me.lordsaad.modeoff.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import me.lordsaad.modeoff.api.Plot;
import me.lordsaad.modeoff.api.PlotRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by LordSaad.
 */
@PacketRegister(Side.CLIENT)
public class PacketSyncPlots extends PacketBase {

	private HashSet<Plot> plots;

	public PacketSyncPlots() {}

	public PacketSyncPlots(HashSet<Plot> plots) {
		this.plots = plots;
	}

	@SaveMethodSetter(saveName = "manual_saver")
	private void manualSaveSetter(NBTTagCompound compound) {
		plots = new HashSet<>();
		if (compound == null) return;
		NBTTagList list = compound.getTagList("list", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			NBTTagCompound compound1 = list.getCompoundTagAt(i);
			Plot plot = new Plot();
			plot.deserializeNBT(compound1);
			plots.add(plot);
		}
	}

	@SaveMethodGetter(saveName = "manual_saver")
	private NBTTagCompound manualSaveGetter() {
		NBTTagCompound nbt = new NBTTagCompound();

		if (plots == null) return nbt;

		NBTTagList list = new NBTTagList();
		for (Plot plot : plots) {
			list.appendTag(plot.serializeNBT());
		}
		nbt.setTag("list", list);
		return nbt;
	}

	@Override
	public void handle(MessageContext messageContext) {
		PlotRegistry.INSTANCE.plots = new ArrayList<>(plots);
	}
}
