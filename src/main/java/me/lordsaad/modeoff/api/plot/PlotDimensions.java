package me.lordsaad.modeoff.api.plot;

import com.teamwizardry.librarianlib.features.math.Vec2d;
import me.lordsaad.modeoff.api.ConfigValues;
import me.lordsaad.modeoff.api.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

public class PlotDimensions implements INBTSerializable<NBTTagCompound> {
	private BlockPos corner1, corner2;

	PlotDimensions(int plotID) {
		Vec2d center = Utils.spiralLocFromID(plotID, Vec2d.ZERO)
				.mul(ConfigValues.plotSize + ConfigValues.plotMarginWidth)
				.add(ConfigValues.firstPlotCenterX, ConfigValues.firstPlotCenterZ);

		corner1 = new BlockPos(center.getX() - (ConfigValues.plotSize / 2), ConfigValues.firstPlotCenterY, center.getY() - (ConfigValues.plotSize / 2));
		corner2 = new BlockPos(center.getX() + (ConfigValues.plotSize / 2), ConfigValues.firstPlotCenterY, center.getY() + (ConfigValues.plotSize / 2));
	}

	PlotDimensions() {
	}

	public BlockPos getCorner1() {
		return corner1;
	}

	public BlockPos getCorner2() {
		return corner2;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setLong("corner1", getCorner1().toLong());
		compound.setLong("corner2", getCorner2().toLong());
		return compound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("corner1") && nbt.hasKey("corner2")) {
			corner1 = BlockPos.fromLong(nbt.getLong("corner1"));
			corner2 = BlockPos.fromLong(nbt.getLong("corner2"));
		}
	}

	public Iterable<BlockPos.MutableBlockPos> getAllBlocks() {
		return BlockPos.getAllInBoxMutable(
				new BlockPos(getCorner1().getX(), 0, getCorner1().getZ()),
				new BlockPos(getCorner2().getX(), 255, getCorner2().getZ()));
	}

	public Iterable<BlockPos.MutableBlockPos> getAllBlocks(int expand) {
		int xMin = Math.min(getCorner1().getX(), getCorner2().getX());
		int xMax = Math.max(getCorner1().getX(), getCorner2().getX());
		int zMin = Math.min(getCorner1().getZ(), getCorner2().getZ());
		int zMax = Math.max(getCorner1().getZ(), getCorner2().getZ());

		return BlockPos.getAllInBoxMutable(
				new BlockPos(xMin - expand, 0, zMin - expand),
				new BlockPos(xMax + expand, 255, zMax + expand));
	}

	public boolean isBlockInside(BlockPos pos) {
		int xMin = Math.min(getCorner1().getX(), getCorner2().getX());
		int xMax = Math.max(getCorner1().getX(), getCorner2().getX());
		int zMin = Math.min(getCorner1().getZ(), getCorner2().getZ());
		int zMax = Math.max(getCorner1().getZ(), getCorner2().getZ());

		return pos.getX() >= xMin
				&& pos.getX() <= xMax
				&& pos.getZ() >= zMin
				&& pos.getZ() <= zMax;
	}
}
