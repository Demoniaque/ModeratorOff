package me.lordsaad.modeoff.common.network;

import com.teamwizardry.librarianlib.features.network.PacketBase;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.PlotAssigningManager;
import me.lordsaad.modeoff.api.PlotManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by LordSaad.
 */
public class PacketManagerGui extends PacketBase {

	public PacketManagerGui() {
	}

	@Override
	public void handle(MessageContext messageContext) {
		BlockPos pos = PlotManager.getPlotPos(PlotAssigningManager.INSTANCE.getPlotForUUID(Minecraft.getMinecraft().player.getUniqueID()));
		if (pos == null) return;
		Minecraft.getMinecraft().player.openGui(ModeratorOff.instance, 0, Minecraft.getMinecraft().player.world, pos.getX(), pos.getY(), pos.getZ());

	}
}
