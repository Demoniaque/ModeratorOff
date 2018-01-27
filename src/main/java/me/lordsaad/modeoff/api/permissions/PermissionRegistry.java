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
		registerPermission(PERMISSION_ENABLE_BLOCK_BREAKING);
		registerPermission(PERMISSION_ENABLE_BLOCK_PLACING);
		registerPermission(PERMISSION_PLOT_ADMIN);
		registerPermission(PERMISSION_PLOT_REGISTER);
		registerPermission(PERMISSION_DISABLE_FLIGHT);
		registerPermission(PERMISSION_GAMEMODE_CREATIVE);
		registerPermission(PERMISSION_GAMEMODE_SPECTATOR);
		registerPermission(PERMISSION_GAMEMODE_SURVIVAL);
		registerPermission(PERMISSION_GAMEMODE_ADVENTURE);
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

	public static final class DefaultPermissions {
		public static final PermissionLockPlot PERMISSION_LOCK_PLOT = new PermissionLockPlot();
		public static final PermissionBlockBreaking PERMISSION_ENABLE_BLOCK_BREAKING = new PermissionBlockBreaking();
		public static final PermissionBlockPlacing PERMISSION_ENABLE_BLOCK_PLACING = new PermissionBlockPlacing();
		public static final PermissionPlotAdmin PERMISSION_PLOT_ADMIN = new PermissionPlotAdmin();
		public static final PermissionPlotRegister PERMISSION_PLOT_REGISTER = new PermissionPlotRegister();
		public static final PermissionFlight PERMISSION_DISABLE_FLIGHT = new PermissionFlight();
		public static final PermissionGamemode PERMISSION_GAMEMODE = new PermissionGamemode();
		public static final PermissionGamemodeCreative PERMISSION_GAMEMODE_CREATIVE = new PermissionGamemodeCreative();
		public static final PermissionGamemodeSpectator PERMISSION_GAMEMODE_SPECTATOR = new PermissionGamemodeSpectator();
		public static final PermissionGamemodeSurvival PERMISSION_GAMEMODE_SURVIVAL = new PermissionGamemodeSurvival();
		public static final PermissionGamemodeAdventure PERMISSION_GAMEMODE_ADVENTURE = new PermissionGamemodeAdventure();
	}
}
