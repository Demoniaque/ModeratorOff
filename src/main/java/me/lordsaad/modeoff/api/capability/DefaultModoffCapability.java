package me.lordsaad.modeoff.api.capability;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.saving.Savable;
import com.teamwizardry.librarianlib.features.saving.Save;
import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.rank.IRank;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.common.network.PacketUpdateCaps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;

/**
 * Created by Saad on 8/16/2016.
 */
@Savable
public class DefaultModoffCapability implements IModoffCapability {

	@Save
	private int rankID = 1;

	@Nullable
	private Plot enclosingPlot;

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

	@Nullable
	@Override
	public Plot getEnclosingPlot() {
		return enclosingPlot;
	}

	@Override
	public void setEnclosingPlot(@Nullable Plot enclosingPlot) {
		this.enclosingPlot = enclosingPlot;
	}

	@Override
	public void dataChanged(Entity entity) {
		if (entity instanceof EntityPlayerMP)
			PacketHandler.NETWORK.sendTo(new PacketUpdateCaps(ModoffCapabilityProvider.capability().writeNBT(this, null)), (EntityPlayerMP) entity);
	}
}
