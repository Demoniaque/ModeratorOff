package me.lordsaad.modeoff.api;

import me.lordsaad.modeoff.ModeratorOff;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class PlotManager {

	public UUID uuid;
	@Nullable
	public EntityPlayer player;
	public int plotID;
	public BlockPos corner1, corner2;

	public PlotManager(@NotNull EntityPlayer player) {
		this.uuid = player.getUniqueID();
		this.player = player;

		PlotAssigningManager manager = PlotAssigningManager.INSTANCE;
		plotID = manager.getPlotForUUID(uuid);
		initPlot();
	}

	public PlotManager(World world, int plotID) {
		this.plotID = plotID;
		initPlot();

		UUID uuid = PlotAssigningManager.INSTANCE.getUUIDForPlot(plotID);
		if (uuid == null) {
			ModeratorOff.logger.error("uuid for plot " + plotID + " could not be found.");
			return;
		}
		this.uuid = uuid;

		player = world.getPlayerEntityByUUID(uuid);
	}

	public static void teleportToPlot(EntityPlayer player, int plotID) {
		if (plotID < 0) return;
		if (player == null) return;

		if (player.world.provider.getDimension() != ConfigValues.plotWorldDimensionID)
			player.changeDimension(ConfigValues.plotWorldDimensionID);

		BlockPos pos = getPlotPos(plotID);
		if (pos == null) return;

		player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5);
	}

	@Nullable
	public static BlockPos getPlotPos(int plotID) {
		if (plotID < 0) return null;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(ConfigValues.x, ConfigValues.y, ConfigValues.z);
		pos.add(ConfigValues.plotSize / 2, ConfigValues.plotSize / 2, ConfigValues.plotSize / 2);

		int row = plotID / ConfigValues.gridRows;
		int column = plotID % ConfigValues.gridColumns;

		pos.move(EnumFacing.valueOf(ConfigValues.directionOfRows), row * ConfigValues.plotSize + row * ConfigValues.plotMarginWidth + ConfigValues.plotSize / 2);
		pos.move(EnumFacing.valueOf(ConfigValues.directionOfColumns), column * ConfigValues.plotSize + column * ConfigValues.plotMarginWidth + ConfigValues.plotSize / 2);
		return new BlockPos(pos);
	}

	private void initPlot() {
		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(ConfigValues.x, ConfigValues.y, ConfigValues.z);
		pos.add(ConfigValues.plotSize / 2, ConfigValues.plotSize / 2, ConfigValues.plotSize / 2);

		int row = plotID / ConfigValues.gridRows;
		int column = plotID % ConfigValues.gridColumns;

		pos.move(EnumFacing.valueOf(ConfigValues.directionOfRows), row * ConfigValues.plotSize + row * ConfigValues.plotMarginWidth);
		pos.move(EnumFacing.valueOf(ConfigValues.directionOfColumns), column * ConfigValues.plotSize + column * ConfigValues.plotMarginWidth);
		corner1 = new BlockPos(pos.getX() - (ConfigValues.plotSize / 2), pos.getY(), pos.getZ() - (ConfigValues.plotSize / 2));
		corner2 = new BlockPos(pos.getX() + (ConfigValues.plotSize / 2), pos.getY(), pos.getZ() + (ConfigValues.plotSize / 2));
	}

	public void teleportToCenter() {
		teleportToPlot(player, plotID);
	}

}
