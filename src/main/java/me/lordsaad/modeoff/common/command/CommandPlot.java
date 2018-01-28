package me.lordsaad.modeoff.common.command;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.common.CommonProxy;
import me.lordsaad.modeoff.common.network.PacketOpenGuiPlot;
import me.lordsaad.modeoff.common.network.PacketSyncPlots;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by LordSaad.
 */
public class CommandPlot extends CommandBase {

	@NotNull
	@Override
	public String getName() {
		return "plot";
	}

	@NotNull
	@Override
	public String getUsage(@NotNull ICommandSender sender) {
		return "/plot <register <mod name> / tp [plotID/player] / manage [plot name] / rename>";
	}

	@Override
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) throws CommandException {
		if (args.length == 0) {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect command usage."));
			sender.sendMessage(new TextComponentString(TextFormatting.GRAY + getUsage(sender)));
		} else {
			String firstArg = args[0].toLowerCase(Locale.ROOT);

			switch (firstArg) {

				//case "reset": {
				//	if (!RankRegistry.INSTANCE.getRank(getCommandSenderAsPlayer(sender)).hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_REGISTER)) {
				//		sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient Permissions. " + TextFormatting.GRAY + "You can't register a plot to edit or manage."));
				//		return;
				//	}
//
				//	EntityPlayer player = getCommandSenderAsPlayer(sender);
				//	Plot plot = PlotRegistry.INSTANCE.getPlot(player.getUniqueID());
				//	if (plot != null) {
//
//
				//	} else {
				//		sender.sendMessage(new TextComponentString(TextFormatting.RED + "Plot does not exist. " + TextFormatting.GRAY + "You do not have a registered plot."));
				//	}
				//}

				case "delete": {
					if (!RankRegistry.INSTANCE.getRank(getCommandSenderAsPlayer(sender)).hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_REGISTER)) {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient Permissions. " + TextFormatting.RED + "You can't own a plot."));
						return;
					}

					if (args.length > 1) {
						for (Plot plot : PlotRegistry.INSTANCE.plots) {
							if (plot.getModName().equals(args[1].toLowerCase(Locale.ROOT))) {

								if (!RankRegistry.INSTANCE.hasPermission(getCommandSenderAsPlayer(sender), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN)
										&& !plot.getOwners().contains(getCommandSenderAsPlayer(sender).getUniqueID())) {
									sender.sendMessage(new TextComponentString(TextFormatting.RED + "You are not an owner of that plot."));
									return;
								}

								PlotRegistry.INSTANCE.deletePlot(plot);
								PacketHandler.NETWORK.sendToAll(new PacketSyncPlots(PlotRegistry.INSTANCE.plots));
								sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Plot deleted successfully."));

								return;
							}
						}
					}
					EntityPlayer player = getCommandSenderAsPlayer(sender);
					Plot plot = PlotRegistry.INSTANCE.getPlot(player.getUniqueID());
					if (plot != null) {

						PlotRegistry.INSTANCE.deletePlot(plot);
						PacketHandler.NETWORK.sendToAll(new PacketSyncPlots(PlotRegistry.INSTANCE.plots));
						sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Plot deleted successfully."));

					} else {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have a registered plot."));
					}

					return;
				}

				case "rename": {
					if (!RankRegistry.INSTANCE.getRank(getCommandSenderAsPlayer(sender)).hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_REGISTER)) {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient Permissions. " + TextFormatting.GRAY + "You can't register a plot to edit or manage."));
						return;
					}

					EntityPlayer player = getCommandSenderAsPlayer(sender);
					Plot plot = PlotRegistry.INSTANCE.getPlot(player.getUniqueID());
					if (plot != null) {

						if (args.length <= 1) throw new WrongUsageException(getUsage(sender));

						String newModName = args[1];
						String oldModName = plot.getModName();

						if (newModName.equals(oldModName)) {
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Old mod name is the same as the new one specified."));
							return;
						}

						plot.setModName(newModName);
						PlotRegistry.INSTANCE.savePlot(plot.getID());
						PacketHandler.NETWORK.sendToAll(new PacketSyncPlots(PlotRegistry.INSTANCE.plots));

					} else {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Plot does not exist. " + TextFormatting.GRAY + "You do not have a registered plot."));
					}
					return;
				}

				case "manage": {
					if (!RankRegistry.INSTANCE.getRank(getCommandSenderAsPlayer(sender)).hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_REGISTER)) {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient Permissions. " + TextFormatting.GRAY + "You can't register a plot to edit or manage."));
						return;
					}

					if (args.length > 1) {
						for (Plot plot : PlotRegistry.INSTANCE.plots) {
							if (plot.getModName().equals(args[1].toLowerCase(Locale.ROOT))) {

								if (!RankRegistry.INSTANCE.hasPermission(getCommandSenderAsPlayer(sender), PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN)
										&& !plot.getOwners().contains(getCommandSenderAsPlayer(sender).getUniqueID())) {
									sender.sendMessage(new TextComponentString(TextFormatting.RED + "You are not an owner of that plot."));
									return;
								}

								PacketHandler.NETWORK.sendTo(new PacketOpenGuiPlot(plot.getID()), getCommandSenderAsPlayer(sender));
								return;
							}
						}
					}
					EntityPlayer player = getCommandSenderAsPlayer(sender);
					Plot plot = PlotRegistry.INSTANCE.getPlot(player.getUniqueID());
					if (plot != null) {

						PacketHandler.NETWORK.sendTo(new PacketOpenGuiPlot(), getCommandSenderAsPlayer(sender));

					} else {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have a registered plot."));
					}
					return;
				}

				case "tp": {
					if (args.length == 1) {
						if (!RankRegistry.INSTANCE.getRank(getCommandSenderAsPlayer(sender)).hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_REGISTER)) {
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient Permissions. " + TextFormatting.RED + "You can't own a plot to teleport to. Specify a username or mod name to teleport to someone else's plot instead."));
							return;
						}

						Plot plot = PlotRegistry.INSTANCE.getPlot(getCommandSenderAsPlayer(sender).getUniqueID());
						if (plot != null) {
							plot.teleport(getCommandSenderAsPlayer(sender));
							sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "Teleported to plot [" + TextFormatting.GOLD + plot.getModName() + TextFormatting.GRAY + "] successfully."));

						} else {
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have a registered plot."));
							return;
						}

					} else {
						for (Plot plot : PlotRegistry.INSTANCE.plots) {
							if (plot.getModName().equals(args[1].toLowerCase(Locale.ROOT))) {
								plot.teleport(getCommandSenderAsPlayer(sender));
								sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "Teleported to plot [" + TextFormatting.GOLD + plot.getModName() + TextFormatting.GRAY + "] successfully."));
								return;
							}
						}

						if (!CommonProxy.playerUUIDMap.containsKey(args[1])) {
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "No plot or player named [" + TextFormatting.GOLD + args[1] + TextFormatting.RED + "] could be found."));
							return;
						}

						Plot plot = PlotRegistry.INSTANCE.getPlot(CommonProxy.playerUUIDMap.get(args[1]));
						if (plot != null) {
							plot.teleport(getCommandSenderAsPlayer(sender));
							sender.sendMessage(new TextComponentString(TextFormatting.GRAY + "Teleported to plot [" + TextFormatting.GOLD + plot.getModName() + TextFormatting.GRAY + "] successfully."));

						} else {
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "No plot or player named [" + TextFormatting.GOLD + args[1] + TextFormatting.RED + "] could be found."));
							return;
						}
					}
					return;
				}

				case "register": {

					if (!RankRegistry.INSTANCE.getRank(getCommandSenderAsPlayer(sender)).hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_REGISTER)) {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient Permissions. " + TextFormatting.GRAY + "You can't register a plot for someone else."));
						return;
					}

					if (args.length < 2)
						throw new WrongUsageException(getUsage(sender));
					else {
						String modName = args[1];

						EntityPlayer player = getCommandSenderAsPlayer(sender);

						if (!RankRegistry.INSTANCE.hasPermission(player, PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_ADMIN)
								&& PlotRegistry.INSTANCE.isUUIDRegistered(player.getUniqueID())) {
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "You already have a plot. " + TextFormatting.GRAY + "Do /plot tp to teleport to it."));
							return;
						}

						for (Plot plot : PlotRegistry.INSTANCE.plots) {
							if (plot.getModName().equalsIgnoreCase(modName)) {
								sender.sendMessage(new TextComponentString(TextFormatting.RED + "A plot with that name is already registered! Either try another name or contact an Admin."));
								return;
							}
						}

						Set<UUID> owners = new HashSet<>();
						for (Set<UUID> team : CommonProxy.teams) {
							for (UUID uuid : team) {
								if (uuid.equals(player.getUniqueID())) {
									owners = team;
									break;
								}
							}
						}

						if (owners.isEmpty()) {
							owners.add(player.getUniqueID());
						}

						Plot plot = PlotRegistry.INSTANCE.registerPlot(new Plot(modName, owners));

						PacketHandler.NETWORK.sendToAll(new PacketSyncPlots(PlotRegistry.INSTANCE.plots));

						plot.teleport(player);

						player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Your plot has been registered successfully! " + TextFormatting.GRAY + " Plot ID: [" + TextFormatting.GREEN + plot.getID() + TextFormatting.GRAY + "]"));

						if (owners.size() > 1) {

							List<UUID> ownerList = new ArrayList<>(owners);
							StringBuilder builder = new StringBuilder();
							for (int i = 0; i < ownerList.size(); i++) {
								UUID uuid = ownerList.get(i);
								String playerName = CommonProxy.playerUUIDMap.inverse().get(uuid);

								builder.append(playerName);

								if (i < ownerList.size()) builder.append(", ");
							}

							player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Team Members Included: " + TextFormatting.GRAY + builder.toString()));
						}

						if (!sender.getName().equals(player.getName()))
							sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Plot for '" + TextFormatting.GOLD + player.getName() + TextFormatting.GREEN + "' has been registered successfully! " + TextFormatting.GRAY + " Plot ID: [" + TextFormatting.GREEN + plot.getID() + TextFormatting.GRAY + "]"));
					}

					return;
				}

				default: {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect command usage."));
					sender.sendMessage(new TextComponentString(TextFormatting.GRAY + getUsage(sender)));
				}
			}
		}
	}
}
