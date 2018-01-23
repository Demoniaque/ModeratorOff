package me.lordsaad.modeoff.api.rank.defaultranks;

import me.lordsaad.modeoff.api.rank.EnumPerm;
import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class RankJudge implements IRank {

	@Override
	@Nullable
	public String getName() {
		return "Judge";
	}

	@Override
	public TextFormatting getColor() {
		return TextFormatting.GOLD;
	}

	@Override
	public EnumPerm getPermission() {
		return EnumPerm.ADMIN;
	}

	@Override
	public boolean displaySeparately() {
		return true;
	}
}
