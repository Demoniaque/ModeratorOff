package me.lordsaad.modeoff.api.rank;

import net.minecraft.util.text.TextFormatting;

/**
 * Created by LordSaad.
 */
public interface IRank {

	String getName();

	TextFormatting getColor();

	EnumPerm getPermission();

	boolean displaySeparately();
}
