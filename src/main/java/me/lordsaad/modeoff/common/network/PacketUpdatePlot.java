package me.lordsaad.modeoff.common.network;

import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Objects;

/**
 * Created by LordSaad.
 */
@PacketRegister(Side.SERVER)
public class PacketUpdatePlot extends PacketBase {

	private Plot plot;

	public PacketUpdatePlot() {
	}

	public PacketUpdatePlot(Plot plot) {
		this.plot = plot;
	}

	@SaveMethodSetter(saveName = "manual_saver")
	private void manualSaveSetter(NBTTagCompound compound) {
		if (compound != null) {
			plot = Plot.deserialize(compound);
		}
	}

	@SaveMethodGetter(saveName = "manual_saver")
	private NBTTagCompound manualSaveGetter() {
		if (plot == null) return new NBTTagCompound();
		return plot.serializeNBT();
	}

	@Override
	public void handle(MessageContext messageContext) {
		if (plot == null) return;
		Objects.requireNonNull(PlotRegistry.INSTANCE.getPlot(plot.getID())).deserializeNBT(plot.serializeNBT());
		PlotRegistry.INSTANCE.savePlot(plot.getID());
	}
}
