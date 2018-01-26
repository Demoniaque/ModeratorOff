package me.lordsaad.modeoff.api;

import net.minecraft.util.math.BlockPos;

public class PlotLocationUtils {

	private static int PLOT_SIZE = 34;
	private static int PLOT_MARGIN = 11;
	private static int GRID_CENTER_X = 864;
	private static int GRID_CENTER_Y = 35;
	private static int GRID_CENTER_Z = 38;

	private static BlockPos spiralLocFromID(int plotID) {
		// (dx, dy) is a vector - direction in which we move right now
		int dx = 0;
		int dy = 1;
		// length of current segment
		int segment_length = 1;

		// current position (x, z) and how much of current segment we passed
		int x = 0;
		int z = 0;
		int segment_passed = 0;
		if (plotID == 0) {
			return new BlockPos(x, 0, z);
		}
		for (int n = 0; n < plotID; ++n) {
			// make a step, add 'direction' vector (dx, dy) to current position (x, z)
			x += dx;
			z += dy;
			++segment_passed;

			if (segment_passed == segment_length) {
				// done with current segment
				segment_passed = 0;

				// 'rotate' directions
				int buffer = dy;
				dy = -dx;
				dx = buffer;

				// increase segment length if necessary
				if (dx == 0) {
					++segment_length;
				}
			}
		}
		return new BlockPos(x, 0, z);
	}

	public class PlotDimensions {
		private final BlockPos corner1, corner2, center;

		public PlotDimensions(int plotID) {
			BlockPos originCenter = spiralLocFromID(plotID);

			int buffer = PLOT_SIZE + PLOT_MARGIN;

			center = new BlockPos(
					(originCenter.getX() * buffer) + GRID_CENTER_X,
					GRID_CENTER_Y,
					(originCenter.getZ() * buffer) + GRID_CENTER_Z);

			corner1 = new BlockPos(center.getX() - (PLOT_SIZE / 2), GRID_CENTER_Y, center.getZ() - (PLOT_SIZE / 2));
			corner2 = new BlockPos(center.getX() + (PLOT_SIZE / 2), GRID_CENTER_Y, center.getZ() + (PLOT_SIZE / 2));
		}

		public BlockPos getCorner1() {
			return corner1;
		}

		public BlockPos getCorner2() {
			return corner2;
		}

		public BlockPos getCenter() {
			return center;
		}
	}
}
