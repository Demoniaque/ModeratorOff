package me.lordsaad.modeoff.api.rank.defaultranks;

import me.lordsaad.modeoff.api.permissions.Permission;
import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;

public class RankSponsor implements IRank {

	private HashSet<Permission> permissions = new HashSet<>();

	public RankSponsor() {
	}

	@Override
	@Nullable
	public String getName() {
		return "Sponsor";
	}

	@Override
	public TextFormatting getColor() {
		return TextFormatting.AQUA;
	}

	@Override
	public boolean displaySeparately() {
		return true;
	}

	@Override
	public Collection<Permission> getPermissions() {
		return null;
	}

	@Override
	public void addPermission(Permission permission) {

	}

	@Override
	public void removePermission(Permission permission) {

	}
}
