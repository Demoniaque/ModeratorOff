package me.lordsaad.modeoff.api.permissions.defaultperms;

import me.lordsaad.modeoff.api.permissions.Permission;

public class PermissionLockPlot implements Permission {

	public PermissionLockPlot() {
	}

	@Override
	public String getTagID() {
		return "lock_plot";
	}
}
