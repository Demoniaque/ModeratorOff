package me.lordsaad.modeoff.api.permissions.defaultperms;

import me.lordsaad.modeoff.api.permissions.Permission;

public class PermissionBlockBreaking implements Permission {

	public PermissionBlockBreaking() {
	}

	@Override
	public String getTagID() {
		return "block_breaking";
	}

}
