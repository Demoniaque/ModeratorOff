package me.lordsaad.modeoff.api.rank.defaultranks;

import me.lordsaad.modeoff.api.permissions.Permission;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;
import java.util.HashSet;

public class RankAdmin implements IRank {

	private HashSet<Permission> permissions = new HashSet<>();

	public RankAdmin() {
		permissions.add(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN);
		permissions.add(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_REGISTER);
	}

	@Override
	public String getName() {
		return "Admin";
	}

	@Override
	public TextFormatting getColor() {
		return TextFormatting.DARK_RED;
	}

	@Override
	public boolean displaySeparately() {
		return true;
	}

	@Override
	public Collection<Permission> getPermissions() {
		return permissions;
	}

	@Override
	public void addPermission(Permission permission) {
	}

	@Override
	public void removePermission(Permission permission) {
	}
}
