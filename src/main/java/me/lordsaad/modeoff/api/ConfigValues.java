package me.lordsaad.modeoff.api;

import com.teamwizardry.librarianlib.features.config.ConfigProperty;

/**
 * Created by LordSaad.
 */
public final class ConfigValues {

	@ConfigProperty(category = "general")
	public static double spawnX = 22.5;
	@ConfigProperty(category = "general")
	public static double spawnY = 50;
	@ConfigProperty(category = "general")
	public static double spawnZ = 9.5;

	@ConfigProperty(category = "general")
	public static double plotX = 886.5;
	@ConfigProperty(category = "general")
	public static double plotY = 50;
	@ConfigProperty(category = "general")
	public static double plotZ = 60.5;

	@ConfigProperty(category = "general")
	public static int firstPlotCenterX = 863;
	@ConfigProperty(category = "general")
	public static int firstPlotCenterY = 35;
	@ConfigProperty(category = "general")
	public static int firstPlotCenterZ = 37;
	@ConfigProperty(category = "general")
	public static int plotSize = 35;
	@ConfigProperty(category = "general")
	public static int plotMarginWidth = 11;
	@ConfigProperty(category = "general")
	public static int plotWorldDimensionID = 0;

	@ConfigProperty(category = "general", comment = "If disabled, will disable the plot and rank system.")
	public static boolean enablePlotsAndRanks = false;
}
