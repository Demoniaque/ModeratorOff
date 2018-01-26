package me.lordsaad.modeoff.api.rank.defaultranks;

import me.lordsaad.modeoff.api.permissions.Permission;
import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.util.text.TextFormatting;

import java.util.HashSet;
import java.util.Set;

public class RankNormal implements IRank {

	private Set<Permission> permissions = new HashSet<>();

	public RankNormal() {
	}

	@Override
	public String getName() {
		return "Normal";
	}

	@Override
	public TextFormatting getColor() {
		return TextFormatting.DARK_GRAY;
	}

	@Override
	public boolean displaySeparately() {
		return false;
	}

	@Override
	public Set<Permission> getPermissions() {
		return permissions;
	}

	@Override
	public void addPermission(Permission permission) {

	}

	@Override
	public void removePermission(Permission permission) {

	}
}
