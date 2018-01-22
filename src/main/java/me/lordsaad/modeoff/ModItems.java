package me.lordsaad.modeoff;

import me.lordsaad.modeoff.common.items.ItemSpeed;
import me.lordsaad.modeoff.common.items.ItemTeleport;

public class ModItems {

	public static ItemSpeed SPEED;
	public static ItemTeleport TELEPORT;

	public static void init() {
		SPEED = new ItemSpeed();
		TELEPORT = new ItemTeleport();
	}
}
