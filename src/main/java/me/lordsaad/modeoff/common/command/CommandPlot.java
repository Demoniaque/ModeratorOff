package me.lordsaad.modeoff.common.command;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.common.network.PacketOpenGuiPlot;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

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
		return "/plot <register / tp [plotID/player] / add <team member>>";
	}

	@Override
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) throws CommandException {
		if (args.length == 0) {
			sender.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect command usage."));
			sender.sendMessage(new TextComponentString(TextFormatting.GRAY + getUsage(sender)));
		} else {
			String firstArg = args[0].toLowerCase();

			switch (firstArg) {

				case "edit":
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

				case "teleport":
				case "tp": {
					EntityPlayer player;
					if (args.length == 1) {
						if (!RankRegistry.INSTANCE.getRank(getCommandSenderAsPlayer(sender)).hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_PLOT_REGISTER)) {
							sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient Permissions. " + TextFormatting.GRAY + "You can't own a plot to teleport to. Specify a username to teleport to their plot instead."));
							return;
						}
						player = getCommandSenderAsPlayer(sender);
					} else player = getPlayer(server, sender, args[1]);

					Plot plot = PlotRegistry.INSTANCE.getPlot(player.getUniqueID());
					if (plot != null) {
						plot.teleportToPlot(getCommandSenderAsPlayer(sender));

						sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Teleported to [" + TextFormatting.GOLD + player.getName() + "]'s plot successfully."));

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

					EntityPlayer player;
					if (args.length == 1)
						player = getCommandSenderAsPlayer(sender);
					else if (!(sender instanceof EntityPlayer) || RankRegistry.INSTANCE.isAdmin((EntityPlayer) sender))
						player = getPlayer(server, sender, args[1]);
					else {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient Permissions. " + TextFormatting.GRAY + "You can't register a plot for someone else."));
						return;
					}

					if (PlotRegistry.INSTANCE.isUUIDRegistered(player.getUniqueID())) {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "You already have a plot. " + TextFormatting.GRAY + "Do /plot tp to teleport to it."));
						return;
					}

					Plot plot = PlotRegistry.INSTANCE.registerPlot(new Plot(player.getUniqueID()));
					plot.teleportToPlot(player);

					player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Your plot has been registered successfully! " + TextFormatting.GRAY + " Plot ID: [" + TextFormatting.GREEN + plot.getID() + TextFormatting.GRAY + "]"));
					if (!sender.getName().equals(player.getName()))
						sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Plot for '" + TextFormatting.GOLD + player.getName() + TextFormatting.GREEN + "' has been registered successfully! " + TextFormatting.GRAY + " Plot ID: [" + TextFormatting.GREEN + plot.getID() + TextFormatting.GRAY + "]"));

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
