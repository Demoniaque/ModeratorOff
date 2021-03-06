package me.lordsaad.modeoff.common;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import me.lordsaad.modeoff.ModItems;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.ConfigValues;
import me.lordsaad.modeoff.api.capability.IModoffCapability;
import me.lordsaad.modeoff.api.capability.ModoffCapabilityProvider;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.IRank;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.common.network.PacketSyncPlots;
import me.lordsaad.modeoff.common.network.PacketSyncRanks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.GameType;
import net.minecraft.world.Teleporter;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created by LordSaad.
 */
public class EventHandler {

	@SubscribeEvent
	public void joinWorld(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;

		if (ConfigValues.enablePlotsAndRanks && event.player instanceof EntityPlayerMP) {
			ModeratorOff.logger.info("About to sync data to " + event.player.getName());
			PacketHandler.NETWORK.sendTo(new PacketSyncPlots(PlotRegistry.INSTANCE.plots), (EntityPlayerMP) event.player);
			PacketHandler.NETWORK.sendTo(new PacketSyncRanks(RankRegistry.INSTANCE.rankMap), (EntityPlayerMP) event.player);
			ModeratorOff.logger.info("Done.");
		}

		// Send log in message
		{
			player.sendMessage(new TextComponentString(
					TextFormatting.GRAY + "<<==============[ " + TextFormatting.GOLD + "ModOff" + TextFormatting.GRAY + " ]==============>>"));
			player.sendMessage(new TextComponentString(" "));
			player.sendMessage(new TextComponentString(TextFormatting.GRAY + "Welcome " + player.getName() + "!"));
			player.sendMessage(new TextComponentString(" "));

			if (RankRegistry.INSTANCE.hasRank(player, RankRegistry.DefaultRanks.CONTESTANT)) {

				boolean hasPlot = PlotRegistry.INSTANCE.isUUIDRegistered(player.getUniqueID());
				if (hasPlot)
					player.sendMessage(new TextComponentString(TextFormatting.GRAY + "Do /plot tp to teleport to your plot."));
				else
					player.sendMessage(new TextComponentString(TextFormatting.GRAY + "Do /plot register to be automatically assigned a plot."));
			} else {
				player.sendMessage(new TextComponentString(TextFormatting.GRAY + "Use the 2 items in your hotbar to navigate around."));
			}

			player.sendMessage(new TextComponentString(" "));

			player.sendMessage(new TextComponentString(TextFormatting.AQUA + "Click here to join the discord").setStyle(new Style().setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/tu8BpwH"))));
			player.sendMessage(new TextComponentString(" "));

			player.sendMessage(new TextComponentString(
					TextFormatting.GRAY + "<<====================================>>"));
		}

		// Give items
		{
			ItemStack speed = new ItemStack(ModItems.SPEED);
			if (!player.inventory.hasItemStack(speed)) {
				if (!player.addItemStackToInventory(speed)) {
					player.inventory.setInventorySlotContents(0, speed);
				}
			}

			ItemStack teleport = new ItemStack(ModItems.TELEPORT);
			if (!player.inventory.hasItemStack(teleport)) {
				if (!player.addItemStackToInventory(teleport)) {
					player.inventory.setInventorySlotContents(1, teleport);
				}
			}
		}

		// Set capabilities
		{
			boolean isAdmin = RankRegistry.INSTANCE.hasPermission(event.player, PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN);

			if (isAdmin) event.player.setGameType(GameType.CREATIVE);
			else event.player.setGameType(GameType.ADVENTURE);

			if (event.player instanceof EntityPlayerMP) {
				PlayerCapabilities capabilities = event.player.capabilities;

				capabilities.allowFlying = true;
				capabilities.allowEdit = isAdmin;
				capabilities.isFlying = false;

				((EntityPlayerMP) event.player).connection.sendPacket(new SPacketPlayerAbilities(capabilities));
			}
		}

		// Change position and world to spawn
		{
			if (!(player instanceof EntityPlayerMP)) return;
			if (PlotRegistry.INSTANCE.isUUIDRegistered(player.getUniqueID())) return;

			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			if (server == null) return;

			double x = ConfigValues.spawnX, y = ConfigValues.spawnY, z = ConfigValues.spawnZ;

			if (player.world.provider.getDimension() != 0) {
				server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) player, 0, new Teleporter(server.getWorld(0)));
			}
			player.setPosition(x, y, z);
			((EntityPlayerMP) player).connection.setPlayerLocation(x, y, z, 0, 0);

		}
	}

	@SubscribeEvent
	public void respawn(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
		EntityPlayer player = event.player;
		if (player instanceof EntityPlayerMP) {
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

			if (server == null) return;

			double x = ConfigValues.spawnX, y = ConfigValues.spawnY, z = ConfigValues.spawnZ;

			if (player.world.provider.getDimension() != 0) {
				server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) player, 0, new Teleporter(server.getWorld(0)));
			}
			player.setPosition(x, y, z);
			((EntityPlayerMP) player).connection.setPlayerLocation(x, y, z, 0, 0);
		}

		// Give items
		{
			ItemStack speed = new ItemStack(ModItems.SPEED);
			if (!event.player.inventory.hasItemStack(speed)) {
				if (!event.player.addItemStackToInventory(speed)) {
					event.player.inventory.setInventorySlotContents(0, speed);
				}
			}

			ItemStack teleport = new ItemStack(ModItems.TELEPORT);
			if (!event.player.inventory.hasItemStack(teleport)) {
				if (!event.player.addItemStackToInventory(teleport)) {
					event.player.inventory.setInventorySlotContents(1, teleport);
				}
			}
		}
	}

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
		if (event.getWorld().provider.getDimension() != 0) return;

		Plot plot = PlotRegistry.INSTANCE.findPlot(event.getPos());

		boolean isAdmin = RankRegistry.INSTANCE.hasPermission(event.getEntityPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN);

		if (plot != null) {
			boolean enableBlockBreaking = plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_BREAKING);
			boolean plotLocked = plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT);
			boolean isOwner = plot.isOwner(event.getEntityPlayer());

			if (!isAdmin && (plotLocked || (!enableBlockBreaking && !isOwner))) {
				event.setUseItem(Event.Result.DENY);
				event.setUseBlock(Event.Result.DENY);
				event.setCanceled(true);
			}
		} else if (!isAdmin) {
			event.setUseItem(Event.Result.DENY);
			event.setUseBlock(Event.Result.DENY);
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onBreakBlock(BlockEvent.BreakEvent event) {
		if (event.getWorld().provider.getDimension() != 0) return;

		Plot plot = PlotRegistry.INSTANCE.findPlot(event.getPos());

		boolean isAdmin = RankRegistry.INSTANCE.hasPermission(event.getPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN);

		if (plot != null) {
			boolean enableBlockBreaking = plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_BREAKING);
			boolean plotLocked = plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT);
			boolean isOwner = plot.isOwner(event.getPlayer());

			if (!isAdmin && (plotLocked || (!enableBlockBreaking && !isOwner))) {
				event.setCanceled(true);
			}
		} else if (!isAdmin) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void breakSpeed(PlayerEvent.BreakSpeed event) {
		if (event.getEntityPlayer().world.provider.getDimension() != 0) return;

		Plot plot = PlotRegistry.INSTANCE.findPlot(event.getPos());

		boolean isAdmin = RankRegistry.INSTANCE.hasPermission(event.getEntityPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN);

		if (plot != null) {
			boolean enableBlockBreaking = plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_BREAKING);
			boolean plotLocked = plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT);
			boolean isOwner = plot.isOwner(event.getEntityPlayer());

			if (!isAdmin && (plotLocked || (!enableBlockBreaking && !isOwner))) {
				event.setCanceled(true);
			}
		} else if (!isAdmin) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void place(BlockEvent.PlaceEvent event) {
		if (event.getWorld().provider.getDimension() != 0) return;

		Plot plot = PlotRegistry.INSTANCE.findPlot(event.getPos());

		boolean isAdmin = RankRegistry.INSTANCE.hasPermission(event.getPlayer(), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN);

		if (plot != null) {
			boolean enableBlockPlacing = plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_PLACING);
			boolean plotLocked = plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT);
			boolean isOwner = plot.isOwner(event.getPlayer());

			if (!isAdmin && (plotLocked || (!enableBlockPlacing && !isOwner))) {
				event.setCanceled(true);
			}
		} else if (!isAdmin) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.PlayerTickEvent event) {
		if (event.player.world.provider.getDimension() != 0) return;

		if (event.phase == TickEvent.Phase.END && event.side == Side.SERVER && event.player.ticksExisted % 4 == 0) {

			//event.player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 999, 1, true, false));
			event.player.setHealth(20f);
			event.player.getFoodStats().setFoodLevel(20);
			event.player.fallDistance = 0;

			IModoffCapability cap = ModoffCapabilityProvider.getCap(event.player);
			if (cap != null) {
				Plot curPlot = cap.getEnclosingPlot(), plot;
				if (curPlot != (plot = PlotRegistry.INSTANCE.findPlot(event.player.getPosition()))) {
					cap.setEnclosingPlot(plot);
					if (curPlot != null) {
						curPlot.onLeave(event.player);

						boolean isAdmin = RankRegistry.INSTANCE.hasPermission(event.player, PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN);

						if (isAdmin) event.player.setGameType(GameType.CREATIVE);
						else event.player.setGameType(GameType.ADVENTURE);

						if (event.player instanceof EntityPlayerMP) {
							PlayerCapabilities capabilities = event.player.capabilities;

							capabilities.allowFlying = true;
							capabilities.allowEdit = isAdmin;
							capabilities.isFlying = event.player.capabilities.isFlying;

							((EntityPlayerMP) event.player).connection.sendPacket(new SPacketPlayerAbilities(capabilities));
						}
					}

					if (plot != null) {
						plot.onEnter(event.player);

						boolean isAdmin = RankRegistry.INSTANCE.hasPermission(event.player, PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN);

						if (!plot.isOwner(event.player)) {
							if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_CREATIVE)) {
								event.player.setGameType(GameType.CREATIVE);
							} else if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_SPECTATOR)) {
								event.player.setGameType(GameType.SPECTATOR);
							} else if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_SURVIVAL)) {
								event.player.setGameType(GameType.SURVIVAL);
							} else {
								event.player.setGameType(GameType.ADVENTURE);
							}

							if (!isAdmin && event.player instanceof EntityPlayerMP) {
								PlayerCapabilities capabilities = event.player.capabilities;

								capabilities.allowFlying = !plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_DISABLE_FLIGHT);
								capabilities.allowEdit = !plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT);

								((EntityPlayerMP) event.player).connection.sendPacket(new SPacketPlayerAbilities(capabilities));
							}
						} else {
							event.player.setGameType(GameType.CREATIVE);
							if (event.player instanceof EntityPlayerMP) {
								PlayerCapabilities capabilities = event.player.capabilities;

								capabilities.allowFlying = true;
								capabilities.allowEdit = isAdmin || !plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT);

								((EntityPlayerMP) event.player).connection.sendPacket(new SPacketPlayerAbilities(capabilities));
							}
						}
					}
				}
			}
		}
	}
}
