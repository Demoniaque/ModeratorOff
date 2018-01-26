package me.lordsaad.modeoff.api.permissions;

import me.lordsaad.modeoff.api.permissions.defaultperms.*;

import javax.annotation.Nullable;
import java.util.HashSet;

import static me.lordsaad.modeoff.api.permissions.PermissionRegistry.DefaultPermissions.*;

public class PermissionRegistry {

	public static final PermissionRegistry INSTANCE = new PermissionRegistry();

	private final HashSet<Permission> permissions = new HashSet<>();

	private PermissionRegistry() {
		registerPermission(PERMISSION_LOCK_PLOT);
		registerPermission(PERMISSION_DISABLE_BLOCK_BREAKING);
		registerPermission(PERMISSION_DISABLE_BLOCK_PLACING);
		registerPermission(PERMISSION_DISABLE_BLOCK_LEFT_CLICKING);
		registerPermission(PERMISSION_PLOT_ADMIN);
		registerPermission(PERMISSION_PLOT_REGISTER);
	}

	public void registerPermission(Permission permission) {
		permissions.add(permission);
	}

	@Nullable
	public Permission getPermission(String tag) {
		for (Permission permission : permissions) {
			if (permission.getTagID().equals(tag)) return permission;
		}
		return null;
	}

	public boolean hasPermission(IPermissionHolder permissionHolder, Permission permission) {
		return permissionHolder.getPermissions().contains(permission);
	}

	public static class DefaultPermissions {
		public static final PermissionLockPlot PERMISSION_LOCK_PLOT = new PermissionLockPlot();
		public static final PermissionBlockBreaking PERMISSION_DISABLE_BLOCK_BREAKING = new PermissionBlockBreaking();
		public static final PermissionBlockPlacing PERMISSION_DISABLE_BLOCK_PLACING = new PermissionBlockPlacing();
		public static final PermissionBlockLeftClicking PERMISSION_DISABLE_BLOCK_LEFT_CLICKING = new PermissionBlockLeftClicking();
		public static final PermissionPlotAdmin PERMISSION_PLOT_ADMIN = new PermissionPlotAdmin();
		public static final PermissionPlotRegister PERMISSION_PLOT_REGISTER = new PermissionPlotRegister();
	}
}
