package me.lordsaad.modeoff.api.capability;

import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

/**
 * Created by Saad on 8/16/2016.
 */
public interface IModoffCapability {

	int getRankID();

	IRank getRank();

	void setRank(IRank rank);

	void setRank(int rankID);

	@Nullable
	Plot getEnclosingPlot();

	void setEnclosingPlot(@Nullable Plot enclosingPlot);

	void dataChanged(EntityPlayer player);
}
