package me.lordsaad.modeoff.api.rank.defaultranks;

import me.lordsaad.modeoff.api.permissions.Permission;
import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.util.text.TextFormatting;

import java.util.Collection;
import java.util.HashSet;

public class RankNormal implements IRank {

	private HashSet<Permission> permissions = new HashSet<>();

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
