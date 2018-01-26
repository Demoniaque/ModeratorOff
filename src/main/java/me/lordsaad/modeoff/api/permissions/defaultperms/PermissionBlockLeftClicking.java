package me.lordsaad.modeoff.api.permissions.defaultperms;

import me.lordsaad.modeoff.api.permissions.Permission;

public class PermissionBlockLeftClicking implements Permission {

	public PermissionBlockLeftClicking() {
	}

	@Override
	public String getTagID() {
		return "block_left_clicking";
	}
}
