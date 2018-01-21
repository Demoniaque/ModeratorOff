package me.lordsaad.modeoff.api.rank.defaultranks;

import me.lordsaad.modeoff.api.rank.EnumPerm;
import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.util.text.TextFormatting;

public class RankAdmin implements IRank {

	@Override
	public String getName() {
		return "Admin";
	}

	@Override
	public TextFormatting getColor() {
		return TextFormatting.DARK_RED;
	}

	@Override
	public EnumPerm getPermission() {
		return EnumPerm.ADMIN;
	}

}
