package me.lordsaad.modeoff.api.permissions;

import java.util.Collection;

public interface IPermissionHolder {

	Collection<Permission> getPermissions();

	void addPermission(Permission permission);

	void removePermission(Permission permission);

	default boolean hasPermission(Permission permission) {
		return PermissionRegistry.INSTANCE.hasPermission(this, permission);
	}
}
