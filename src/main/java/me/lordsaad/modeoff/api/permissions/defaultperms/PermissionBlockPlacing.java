package me.lordsaad.modeoff.api.permissions.defaultperms;

import me.lordsaad.modeoff.api.permissions.Permission;

public class PermissionBlockPlacing implements Permission {

	public PermissionBlockPlacing() {
	}

	@Override
	public String getTagID() {
		return "block_placing";
	}
}
