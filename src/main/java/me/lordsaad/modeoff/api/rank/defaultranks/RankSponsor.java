package me.lordsaad.modeoff.api.rank.defaultranks;

import me.lordsaad.modeoff.api.rank.EnumPerm;
import me.lordsaad.modeoff.api.rank.IRank;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;

public class RankSponsor implements IRank {

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
	public EnumPerm getPermission() {
		return EnumPerm.ADMIN;
	}
}
