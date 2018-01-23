package me.lordsaad.modeoff.api.capability;

import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Saad on 8/16/2016.
 */
public interface IModoffCapability {

	int getRankID();

	IRank getRank();

	void setRank(IRank rank);

	void setRank(int rankID);

	NBTTagCompound saveNBTData();

	void loadNBTData(NBTTagCompound compound);

	void dataChanged(Entity player);
}
