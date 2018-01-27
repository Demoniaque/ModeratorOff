package me.lordsaad.modeoff.common.network;

import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.Save;
import me.lordsaad.modeoff.api.capability.IModoffCapability;
import me.lordsaad.modeoff.api.capability.ModoffCapabilityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;

@PacketRegister(Side.CLIENT)
public class PacketUpdateCaps extends PacketBase {

	@Save
	private NBTBase tag;

	public PacketUpdateCaps() {
	}

	public PacketUpdateCaps(NBTBase tag) {
		this.tag = tag;
	}

	@Override
	public void handle(@NotNull MessageContext ctx) {
		EntityPlayer player = LibrarianLib.PROXY.getClientPlayer();
		IModoffCapability cap = ModoffCapabilityProvider.getCap(player);
		if (cap != null) {
			ModoffCapabilityProvider.capability().readNBT(cap, null, tag);
		}
	}
}
