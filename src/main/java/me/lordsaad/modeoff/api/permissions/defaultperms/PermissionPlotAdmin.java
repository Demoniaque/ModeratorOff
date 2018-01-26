package me.lordsaad.modeoff.api.permissions.defaultperms;

import me.lordsaad.modeoff.api.permissions.Permission;

public class PermissionPlotAdmin implements Permission {

	public PermissionPlotAdmin() {
	}

	@Override
	public String getTagID() {
		return "plot_admin";
	}
}
