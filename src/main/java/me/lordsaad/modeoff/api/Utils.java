package me.lordsaad.modeoff.api;

import com.teamwizardry.librarianlib.features.math.Vec2d;
import net.minecraft.util.math.BlockPos;

public class Utils {

	public static boolean isWithinBounds(BlockPos corner1, BlockPos corner2, BlockPos pos) {
		int xMin = Math.min(corner1.getX(), corner2.getX());
		int xMax = Math.max(corner1.getX(), corner2.getX());
		int zMin = Math.min(corner1.getZ(), corner2.getZ());
		int zMax = Math.max(corner1.getZ(), corner2.getZ());

		return pos.getX() < xMin
				|| pos.getX() > xMax
				|| pos.getZ() < zMin
				|| pos.getZ() > zMax;
	}

	public static Vec2d spiralLocFromID(int plotID, Vec2d origin) {
		// (dx, dy) is a vector - direction in which we move right now
		int dx = 0;
		int dy = 1;
		// length of current segment
		int segment_length = 1;

		// current position (x, y) and how much of current segment we passed
		int x = origin.getXi();
		int y = origin.getYi();
		int segment_passed = 0;
		if (plotID == 0) {
			return new Vec2d(x, y);
		}
		for (int n = 0; n < plotID; ++n) {
			// make a step, add 'direction' vector (dx, dy) to current position (x, y)
			x += dx;
			y += dy;
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
		return new Vec2d(x, y);
	}
}
