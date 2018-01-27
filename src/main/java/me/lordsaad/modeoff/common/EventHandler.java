package me.lordsaad.modeoff.common;

import me.lordsaad.modeoff.ModItems;
import me.lordsaad.modeoff.api.capability.IModoffCapability;
import me.lordsaad.modeoff.api.capability.ModoffCapabilityProvider;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.IRank;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by LordSaad.
 */
public class EventHandler {

	@SubscribeEvent
	public void chat(ServerChatEvent event) {
		IRank rank = RankRegistry.INSTANCE.getRank(event.getPlayer());

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
		Plot plot = PlotRegistry.INSTANCE.findPlot(event.getPos());

		if (plot != null) {
			if (!RankRegistry.INSTANCE.hasPermission(event.getEntityPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN)
					&& ((!plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_LEFT_CLICKING)
					&& !plot.getOwners().contains(event.getEntityPlayer().getUniqueID()))
					|| plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT))) {
				event.setUseItem(Event.Result.DENY);
				event.setUseBlock(Event.Result.DENY);
				event.setCanceled(true);
			}
		} else if (!RankRegistry.INSTANCE.hasPermission(event.getEntityPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN)) {
			event.setUseItem(Event.Result.DENY);
			event.setUseBlock(Event.Result.DENY);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onBreakBlock(BlockEvent.BreakEvent event) {
		Plot plot = PlotRegistry.INSTANCE.findPlot(event.getPos());

		if (plot != null) {
			if (!RankRegistry.INSTANCE.hasPermission(event.getPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN)
					&& ((!plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_BREAKING)
					&& !plot.getOwners().contains(event.getPlayer().getUniqueID()))
					|| plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT))) {
				event.setCanceled(true);
			}
		} else if (!RankRegistry.INSTANCE.hasPermission(event.getPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed event) {
		Plot plot = PlotRegistry.INSTANCE.findPlot(event.getPos());

		if (plot != null) {
			if (!RankRegistry.INSTANCE.hasPermission(event.getEntityPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN)
					&& ((!plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_BREAKING)
					&& !plot.getOwners().contains(event.getEntityPlayer().getUniqueID()))
					|| plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT))) {
				event.setCanceled(true);
			}
		} else if (!RankRegistry.INSTANCE.hasPermission(event.getEntityPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void place(BlockEvent.PlaceEvent event) {
		Plot plot = PlotRegistry.INSTANCE.findPlot(event.getPos());

		if (plot != null) {
			if (!RankRegistry.INSTANCE.hasPermission(event.getPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN)
					&& ((!plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_PLACING)
					&& !plot.getOwners().contains(event.getPlayer().getUniqueID()))
					|| plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT))) {
				event.setCanceled(true);
			}
		} else if (!RankRegistry.INSTANCE.hasPermission(event.getPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END && event.side == Side.SERVER && event.player.ticksExisted % 4 == 0) {

			//event.player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 999, 1, true, false));
			event.player.setHealth(20f);
			event.player.getFoodStats().setFoodSaturationLevel(20f);
			event.player.getFoodStats().setFoodLevel(20);
			event.player.fallDistance = 0;

			IModoffCapability cap = ModoffCapabilityProvider.getCap(event.player);
			if (cap != null) {
				Plot curPlot = cap.getEnclosingPlot(), plot;
				if (curPlot != (plot = PlotRegistry.INSTANCE.findPlot(event.player.getPosition()))) {
					cap.setEnclosingPlot(plot);
					if (curPlot != null) {
						curPlot.onLeave(event.player);

						event.player.setGameType(GameType.ADVENTURE);

						if (event.player instanceof EntityPlayerMP) {
							PlayerCapabilities capabilities = new PlayerCapabilities();
							capabilities.allowFlying = true;
							capabilities.allowEdit = false;
							capabilities.isFlying = event.player.capabilities.isFlying;
							((EntityPlayerMP) event.player).connection.sendPacket(new SPacketPlayerAbilities(capabilities));
						}
					}

					if (plot != null) {
						plot.onEnter(event.player);

						if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_CREATIVE)) {
							event.player.setGameType(GameType.CREATIVE);
						} else if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_SPECTATOR)) {
							event.player.setGameType(GameType.SPECTATOR);
						} else if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_SURVIVAL)) {
							event.player.setGameType(GameType.SURVIVAL);
						} else {
							event.player.setGameType(GameType.ADVENTURE);
						}

						if (event.player instanceof EntityPlayerMP) {
							PlayerCapabilities capabilities = new PlayerCapabilities();

							capabilities.allowFlying = !plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_DISABLE_FLIGHT);
							capabilities.allowEdit = !plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_BREAKING);

							((EntityPlayerMP) event.player).connection.sendPacket(new SPacketPlayerAbilities(capabilities));
						}
					}
				}
			}
		}
	}
}
