package me.lordsaad.modeoff.api.rank;

import me.lordsaad.modeoff.api.permissions.IPermissionHolder;
import net.minecraft.util.text.TextFormatting;

/**
 * Created by LordSaad.
 */
public interface IRank extends IPermissionHolder {

	String getName();

	TextFormatting getColor();

	boolean displaySeparately();
}
