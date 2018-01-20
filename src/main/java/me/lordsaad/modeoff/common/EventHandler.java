package me.lordsaad.modeoff.common;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.Plot;
import me.lordsaad.modeoff.api.PlotRegistry;
import me.lordsaad.modeoff.common.network.PacketSyncPlots;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;

/**
 * Created by LordSaad.
 */
public class EventHandler {

	@SubscribeEvent
	public void onWorldLoad(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayerMP) {
			ModeratorOff.logger.info("About to send plot data to " + event.getEntity().getName());
			PacketHandler.NETWORK.sendTo(new PacketSyncPlots(new HashSet<>(PlotRegistry.INSTANCE.plots)), (EntityPlayerMP) event.getEntity());
			ModeratorOff.logger.info("Done.");
		}
	}

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
