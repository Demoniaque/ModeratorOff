package me.lordsaad.modeoff.api.permissions.defaultperms;

import me.lordsaad.modeoff.api.permissions.Permission;

public class PermissionPlotRegister implements Permission {

	public PermissionPlotRegister() {
	}

	@Override
	public String getTagID() {
		return "register_plot";
	}
}
