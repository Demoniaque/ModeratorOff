package me.lordsaad.modeoff.api.permissions;

import java.util.Set;

public interface IPermissionHolder {

	Set<Permission> getPermissions();

	void addPermission(Permission permission);

	void removePermission(Permission permission);

	default boolean hasPermission(Permission permission) {
		return PermissionRegistry.INSTANCE.hasPermission(this, permission);
	}
}
