package me.lordsaad.modeoff.common;

import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by LordSaad.
 */
public class EventHandler {

	@SubscribeEvent
	public void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
	//	if (event.getEntityPlayer().getName().startsWith("Player")) return;

		Plot plot = PlotRegistry.INSTANCE.getPlot(event.getEntityPlayer().getUniqueID());

		if (plot == null) {
			event.setUseItem(Event.Result.DENY);
			event.setUseBlock(Event.Result.DENY);
			event.setCanceled(true);
			return;
		}

		Plot.PlotDimensions dimensions = plot.getDimensions();

		if (isWithinBounds(dimensions.getCorner1(), dimensions.getCorner2(), event.getPos())) {
			event.setUseItem(Event.Result.DENY);
			event.setUseBlock(Event.Result.DENY);
			event.setCanceled(true);
		}

		//if (CommonProxy.teamMembers.contains(event.getEntityPlayer().getUniqueID())) return;
		//if (!CommonProxy.contestants.contains(event.getEntityPlayer().getUniqueID())) {
		//	event.setUseItem(Event.Result.DENY);
		//	event.setUseBlock(Event.Result.DENY);
		//	event.setCanceled(true);
		//	return;
		//}
		//PlotManager manager = new PlotManager(event.getEntityPlayer());
		//if (manager.plotID < 0) {
		//	event.setUseItem(Event.Result.DENY);
		//	event.setUseBlock(Event.Result.DENY);
		//	event.setCanceled(true);
		//	return;
		//}
		//if (manager.corner1 == null || manager.corner2 == null) {
		//	event.setUseItem(Event.Result.DENY);
		//	event.setUseBlock(Event.Result.DENY);
		//	event.setCanceled(true);
		//	return;
		//}
//
		//if (isWithinBounds(manager.corner1, manager.corner2, event.getPos())) event.setCanceled(true);
	}

	@SubscribeEvent
	public void onBreakBlock(BlockEvent.BreakEvent event) {
	//	if (event.getPlayer().getName().startsWith("Player")) return;
		Plot plot = PlotRegistry.INSTANCE.getPlot(event.getPlayer().getUniqueID());

		if (plot == null) {
		event.setCanceled(true);
			return;
		}

		Plot.PlotDimensions dimensions = plot.getDimensions();

		if (isWithinBounds(dimensions.getCorner1(), dimensions.getCorner2(), event.getPos())) {
			event.setCanceled(true);
		}

		//if (CommonProxy.teamMembers.contains(event.getPlayer().getUniqueID())) return;
		//if (!CommonProxy.contestants.contains(event.getPlayer().getUniqueID())) {
		//	event.setCanceled(true);
		//	return;
		//}
//
		//PlotManager manager = new PlotManager(event.getPlayer());
		//if (manager.plotID < 0) {
		//	event.setCanceled(true);
		//	return;
		//}
		//if (manager.corner1 == null || manager.corner2 == null) {
		//	event.setCanceled(true);
		//	return;
		//}
//
		//if (isWithinBounds(manager.corner1, manager.corner2, event.getPos())) event.setCanceled(true);
	}

	@SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed event) {
	//	if (event.getEntityPlayer().getName().startsWith("Player")) return;

		Plot plot = PlotRegistry.INSTANCE.getPlot(event.getEntityPlayer().getUniqueID());

		if (plot == null) {
			event.setCanceled(true);
			return;
		}

		Plot.PlotDimensions dimensions = plot.getDimensions();

		if (isWithinBounds(dimensions.getCorner1(), dimensions.getCorner2(), event.getPos())) {
			event.setCanceled(true);
		}

		//if (CommonProxy.teamMembers.contains(event.getEntityPlayer().getUniqueID())) return;
		//if (!CommonProxy.contestants.contains(event.getEntityPlayer().getUniqueID())) {
		//	event.setCanceled(true);
		//	return;
		//}
		//PlotManager manager = new PlotManager(event.getEntityPlayer());
		//if (manager.plotID < 0) {
		//	event.setCanceled(true);
		//	return;
		//}
		//if (manager.corner1 == null || manager.corner2 == null) {
		//	event.setCanceled(true);
		//	return;
		//}
//
		//if (isWithinBounds(manager.corner1, manager.corner2, event.getPos())) event.setCanceled(true);
	}

	@SubscribeEvent
	public void place(BlockEvent.PlaceEvent event) {
	//	if (event.getPlayer().getName().startsWith("Player")) return;

		Plot plot = PlotRegistry.INSTANCE.getPlot(event.getPlayer().getUniqueID());

		if (plot == null) {
			event.setCanceled(true);
			return;
		}

		Plot.PlotDimensions dimensions = plot.getDimensions();

		if (isWithinBounds(dimensions.getCorner1(), dimensions.getCorner2(), event.getPos())) {
			event.setCanceled(true);
		}

		//if (CommonProxy.teamMembers.contains(event.getPlayer().getUniqueID())) return;
		//if (!CommonProxy.contestants.contains(event.getPlayer().getUniqueID())) {
		//	event.setCanceled(true);
		//	return;
		//}
//
		//PlotManager manager = new PlotManager(event.getPlayer());
		//if (manager.plotID < 0) {
		//	event.setCanceled(true);
		//	return;
		//}
		//if (manager.corner1 == null || manager.corner2 == null) {
		//	event.setCanceled(true);
		//	return;
		//}
//
		//if (isWithinBounds(manager.corner1, manager.corner2, event.getPos())) event.setCanceled(true);
	}

	private boolean isWithinBounds(BlockPos corner1, BlockPos corner2, BlockPos pos) {
		int xMin = Math.min(corner1.getX(), corner2.getX());
		int xMax = Math.max(corner1.getX(), corner2.getX());
		int zMin = Math.min(corner1.getZ(), corner2.getZ());
		int zMax = Math.max(corner1.getZ(), corner2.getZ());

		return pos.getX() < xMin
				|| pos.getX() > xMax
				|| pos.getZ() < zMin
				|| pos.getZ() > zMax;
	}
}
