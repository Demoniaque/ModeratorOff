package me.lordsaad.modeoff.api.rank.defaultranks;

import me.lordsaad.modeoff.api.rank.EnumPerm;
import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.util.text.TextFormatting;

public class RankNormal implements IRank {

	@Override
	public String getName() {
		return "Normal";
	}

	@Override
	public TextFormatting getColor() {
		return TextFormatting.GRAY;
	}

	@Override
	public EnumPerm getPermission() {
		return EnumPerm.NONE;
	}
}
