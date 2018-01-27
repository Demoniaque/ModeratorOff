package me.lordsaad.modeoff.api.permissions.defaultperms;

import me.lordsaad.modeoff.api.permissions.Permission;

public class PermissionGamemode implements Permission {

	public PermissionGamemode() {
	}

	@Override
	public String getTagID() {
		return "gamemode";
	}
}
