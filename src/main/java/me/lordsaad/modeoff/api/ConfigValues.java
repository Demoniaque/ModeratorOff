package me.lordsaad.modeoff.api;

import com.teamwizardry.librarianlib.features.config.ConfigProperty;

/**
 * Created by LordSaad.
 */
public final class ConfigValues {

	public static int x = 863;
	public static int y = 35;
	public static int z = 37;
	public static int plotSize = 35;
	public static int plotMarginWidth = 11;
	public static int plotWorldDimensionID = 0;

	@ConfigProperty(category = "general", comment = "If disabled, will disable the plot and rank system.")
	public static boolean enablePlotsAndRanks = false;
}
