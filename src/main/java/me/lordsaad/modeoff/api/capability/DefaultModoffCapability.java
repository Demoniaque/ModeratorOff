package me.lordsaad.modeoff.api.capability;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.librarianlib.features.saving.Save;
import me.lordsaad.modeoff.api.rank.IRank;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.common.network.PacketUpdateCaps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 8/16/2016.
 */
@Savable
public class DefaultModoffCapability implements IModoffCapability {

	@Save
	private int rankID = 1;

	public DefaultModoffCapability() {
	}

	@Override
	public int getRankID() {
		return rankID;
	}

	@Override
	public IRank getRank() {
		return RankRegistry.INSTANCE.ranks.get(getRankID());
	}

	@Override
	public void setRank(IRank rank) {
		rankID = RankRegistry.INSTANCE.ranks.inverse().get(rank);
	}

	@Override
	public void setRank(int rankID) {
		this.rankID = rankID;
	}

	@Override
	public NBTTagCompound saveNBTData() {
		return (NBTTagCompound) ModoffCapabilityStorage.INSTANCE.writeNBT(ModoffCapabilityProvider.modoffCapability, this, null);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		ModoffCapabilityStorage.INSTANCE.readNBT(ModoffCapabilityProvider.modoffCapability, this, null, compound);
	}

	@Override
	public void dataChanged(Entity entity) {
		if ((entity != null) && entity instanceof EntityPlayerMP && !entity.getEntityWorld().isRemote)
			PacketHandler.NETWORK.sendTo(new PacketUpdateCaps(saveNBTData()), (EntityPlayerMP) entity);
	}
}
