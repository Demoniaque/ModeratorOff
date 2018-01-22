package me.lordsaad.modeoff.common.command;

import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
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

				case "add": {
					if (args.length <= 1) {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Incorrect command usage. You didn't specify which player you would like to add to the plot."));
						sender.sendMessage(new TextComponentString(TextFormatting.GRAY + getUsage(sender)));
						return;
					}
					EntityPlayer player = getPlayer(server, sender, args[1]);
					Plot plot = PlotRegistry.INSTANCE.getPlot(getCommandSenderAsPlayer(sender).getUniqueID());
					if (plot != null) {
						plot.getOwners().add(player.getUniqueID());
						sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Successfully added '" + TextFormatting.GOLD + player.getName() + TextFormatting.GREEN + "' to the plot."));
						player.sendMessage(new TextComponentString(TextFormatting.GREEN + "You have been successfully added to " + TextFormatting.GOLD + sender.getName() + TextFormatting.GREEN + "'s plot."));
					} else {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have a registered plot."));
					}

					return;
				}

				case "teleport":
				case "tp": {
					EntityPlayer player;
					if (args.length == 1)
						player = getCommandSenderAsPlayer(sender);
					else player = getPlayer(server, sender, args[1]);

					Plot plot = PlotRegistry.INSTANCE.getPlot(player.getUniqueID());
					if (plot != null) {
						plot.teleportToPlot(getCommandSenderAsPlayer(sender));

						sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Teleported to plot successfully."));

					} else
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have a registered plot."));

					return;
				}

				case "register": {

					EntityPlayer player;
					if (args.length == 1)
						player = getCommandSenderAsPlayer(sender);
					else if (!(sender instanceof EntityPlayer) || RankRegistry.INSTANCE.isAdmin((EntityPlayer) sender))
						player = getPlayer(server, sender, args[1]);
					else {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient Permissions. You can't register a plot for someone else."));
						return;
					}

					if (PlotRegistry.INSTANCE.isUUIDRegistered(player.getUniqueID())) {
						sender.sendMessage(new TextComponentString(TextFormatting.RED + "You already have a plot. Do /plot tp to teleport to it."));
						return;
					}

					HashSet<UUID> owners = new HashSet<>();
					owners.add(player.getUniqueID());

					Plot plot = PlotRegistry.INSTANCE.registerPlot(new Plot(PlotRegistry.INSTANCE.getNextAvailableID(), owners));
					plot.teleportToPlot(player);

					player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Your plot has been registered successfully! " + TextFormatting.GRAY + " Plot ID: [" + plot.getID() + "]"));
					if (!sender.getName().equals(player.getName()))
						sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Plot for '" + TextFormatting.GOLD + player.getName() + TextFormatting.GREEN + "' has been registered successfully! " + TextFormatting.GRAY + " Plot ID: [" + plot.getID() + "]"));

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
