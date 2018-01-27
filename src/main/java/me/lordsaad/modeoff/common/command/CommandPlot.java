package me.lordsaad.modeoff.common.command;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.common.CommonProxy;
import me.lordsaad.modeoff.common.network.PacketOpenGuiPlot;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

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
		return "/plot <register <mod name> / tp [plotID/player] / add <team member>>";
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

					EntityPlayer player = getCommandSenderAsPlayer(sender);
					Plot plot = PlotRegistry.INSTANCE.getPlot(player.getUniqueID());
					if (plot != null) {

						PacketHandler.NETWORK.sendTo(new PacketOpenGuiPlot(), (EntityPlayerMP) player);

					} else {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Plot does not exist. " + TextFormatting.GRAY + "You do not have a registered plot."));
					}
					return;
				}

				case "add": {

					if (!RankRegistry.INSTANCE.getRank(getCommandSenderAsPlayer(sender)).hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_REGISTER)) {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient Permissions. " + TextFormatting.GRAY + "You can't register a plot to add someone to."));
						return;
					}

					if (args.length <= 1) {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect command usage. " + TextFormatting.GRAY + "You didn't specify which player you would like to add to the plot."));
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + getUsage(sender)));
						return;
					}

					if (!CommonProxy.playerUUIDMap.containsKey(args[1])) {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player specified is not a registered contestant."));
						return;
					}

					EntityPlayer player = getPlayer(server, sender, args[1]);
					Plot plot = PlotRegistry.INSTANCE.getPlot(getCommandSenderAsPlayer(sender).getUniqueID());
					if (plot != null) {
						plot.getOwners().add(player.getUniqueID());
						sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully added [" + TextFormatting.GOLD + player.getName() + TextFormatting.GREEN + "] to the plot."));
						player.sendMessage(new TextComponentString(TextFormatting.GREEN + "You have been successfully added to [" + TextFormatting.GOLD + sender.getName() + TextFormatting.GREEN + "]'s plot."));
					} else {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Plot does not exist. " + TextFormatting.GRAY + "You do not have a registered plot."));
					}

					return;
				}

				case "tp": {
					EntityPlayer player;
					if (args.length == 1) {
						if (!RankRegistry.INSTANCE.getRank(getCommandSenderAsPlayer(sender)).hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_REGISTER)) {
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient Permissions. " + TextFormatting.GREEN + "You can't own a plot to teleport to. Specify a username to teleport to their plot instead."));
							return;
						}
						player = getCommandSenderAsPlayer(sender);
					} else player = getPlayer(server, sender, args[1]);

					Plot plot = PlotRegistry.INSTANCE.getPlot(player.getUniqueID());
					if (plot != null) {
						plot.teleport(getCommandSenderAsPlayer(sender));

						sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Teleported to [" + TextFormatting.GOLD + player.getName() + TextFormatting.GRAY + "]'s plot successfully."));

					} else
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Plot does not exist. "
								+ (player.getName().equalsIgnoreCase(sender.getName()) ?
								TextFormatting.GRAY + "You don't have a registered plot." :
								TextFormatting.GRAY + "[" + TextFormatting.GOLD + player.getName() + TextFormatting.GRAY + "] does not have a registered plot.")));

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

						if (PlotRegistry.INSTANCE.isUUIDRegistered(player.getUniqueID())) {
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "You already have a plot. " + TextFormatting.GRAY + "Do /plot tp to teleport to it."));
							return;
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

						plot.teleport(player);

						player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Your plot has been registered successfully! " + TextFormatting.GRAY + " Plot ID: [" + TextFormatting.GREEN + plot.getID() + TextFormatting.GRAY + "]"));
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
