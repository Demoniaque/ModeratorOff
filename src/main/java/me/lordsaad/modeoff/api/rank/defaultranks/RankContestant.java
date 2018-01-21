package me.lordsaad.modeoff.api.rank.defaultranks;

import me.lordsaad.modeoff.api.rank.EnumPerm;
import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.util.text.TextFormatting;

public class RankContestant implements IRank {

	@Override
	public String getName() {
		return "Contestant";
	}

	@Override
	public TextFormatting getColor() {
		return TextFormatting.GREEN;
	}

	@Override
	public EnumPerm getPermission() {
		return EnumPerm.CONTESTANT;
	}
}
