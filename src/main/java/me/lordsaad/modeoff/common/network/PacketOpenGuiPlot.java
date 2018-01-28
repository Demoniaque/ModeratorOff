package me.lordsaad.modeoff.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import me.lordsaad.modeoff.ModeratorOff;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.CLIENT)
public class PacketOpenGuiPlot extends PacketBase {

	@Save
	private int plotID = -1;

	public PacketOpenGuiPlot() {
	}

	public PacketOpenGuiPlot(int plotID) {
		this.plotID = plotID;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		EntityPlayer player = LibrarianLib.PROXY.getClientPlayer();

		player.openGui(ModeratorOff.instance, 0, player.getEntityWorld(), plotID, 0, 0);
	}
}
