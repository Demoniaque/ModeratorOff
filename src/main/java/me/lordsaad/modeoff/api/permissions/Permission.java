package me.lordsaad.modeoff.api.permissions;

import net.minecraft.nbt.NBTTagString;

public interface Permission {

	String getTagID();

	default NBTTagString serializeNBT() {
		return new NBTTagString(getTagID());
	}
}
