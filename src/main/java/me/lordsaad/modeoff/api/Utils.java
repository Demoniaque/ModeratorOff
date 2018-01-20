package me.lordsaad.modeoff.api;

import com.teamwizardry.librarianlib.features.math.Vec2d;

public class Utils {

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
