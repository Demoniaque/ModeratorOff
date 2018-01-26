package me.lordsaad.modeoff.common;

import me.lordsaad.modeoff.ModItems;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.IRank;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
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
	public void chat(ServerChatEvent event) {
		IRank rank = RankRegistry.INSTANCE.getRank(event.getPlayer());

		if (rank == null) return;

		String newMessage = TextFormatting.GRAY + "[";
		if (rank.displaySeparately()) {
			newMessage += rank.getColor() + rank.getName() + " ";
		}
		newMessage += TextFormatting.RESET + event.getUsername() + TextFormatting.GRAY + "] > " + TextFormatting.RESET + event.getMessage();

		event.setComponent(new TextComponentString(newMessage));
	}

	@SubscribeEvent
	public void toss(ItemTossEvent event) {
		if ((event.getEntityItem().getItem().getItem() == ModItems.SPEED
				|| event.getEntityItem().getItem().getItem() == ModItems.TELEPORT)
				&& !event.getPlayer().inventory.hasItemStack(event.getEntityItem().getItem())) {
			event.getPlayer().addItemStackToInventory(event.getEntityItem().getItem().copy());

			event.getPlayer().world.removeEntity(event.getEntityItem());
		}
	}

	@SubscribeEvent
	public void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		//	if (RankRegistry.INSTANCE.isAdmin(event.getEntityPlayer())) return;

		Plot plot = PlotRegistry.INSTANCE.getPlot(event.getEntityPlayer().getUniqueID());
		if (plot == null) plot = PlotRegistry.INSTANCE.findPlot(event.getPos());


		if (plot != null) {
			if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_DISABLE_BLOCK_LEFT_CLICKING)
					|| plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT)) {
				event.setUseItem(Event.Result.DENY);
				event.setUseBlock(Event.Result.DENY);
				event.setCanceled(true);
			}
		} else {
			event.setUseItem(Event.Result.DENY);
			event.setUseBlock(Event.Result.DENY);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onBreakBlock(BlockEvent.BreakEvent event) {
		//	if (RankRegistry.INSTANCE.isAdmin(event.getPlayer())) return;

		Plot plot = PlotRegistry.INSTANCE.getPlot(event.getPlayer().getUniqueID());
		if (plot == null) plot = PlotRegistry.INSTANCE.findPlot(event.getPos());

		if (plot != null) {
			if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_DISABLE_BLOCK_BREAKING)
					|| plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT)) {
				event.setCanceled(true);
			}
		} else {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed event) {
		Plot plot = PlotRegistry.INSTANCE.getPlot(event.getEntityPlayer().getUniqueID());
		if (plot == null) plot = PlotRegistry.INSTANCE.findPlot(event.getPos());

		if (plot != null) {
			if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_DISABLE_BLOCK_BREAKING)
					|| plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT)) {
				event.setCanceled(true);
			}
		} else {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void place(BlockEvent.PlaceEvent event) {
		//	if (RankRegistry.INSTANCE.isAdmin(event.getPlayer())) return;

		Plot plot = PlotRegistry.INSTANCE.getPlot(event.getPlayer().getUniqueID());
		if (plot == null) plot = PlotRegistry.INSTANCE.findPlot(event.getPos());

		if (plot != null) {
			if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_DISABLE_BLOCK_PLACING)
					|| plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT)) {
				event.setCanceled(true);
			}
		} else {
			event.setCanceled(true);
		}
	}
}
