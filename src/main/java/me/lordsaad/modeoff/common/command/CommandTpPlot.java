package me.lordsaad.modeoff.common.command;

import me.lordsaad.modeoff.api.Plot;
import me.lordsaad.modeoff.api.PlotRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class CommandTpPlot extends CommandBase {

	@NotNull
	@Override
	public String getName() {
		return "plot_tp";
	}

	@NotNull
	@Override
	public String getUsage(@NotNull ICommandSender sender) {
		return "/plot_tp [username/plotID]";
	}

	@Override
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) throws CommandException {
		//if (!(sender instanceof EntityPlayer)) {
		//	sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
		//	return;
		//}
//
		//if (!CommonProxy.teamMembers.contains(((EntityPlayer) sender).getUniqueID()) || !CommonProxy.contestants.contains(((EntityPlayer) sender).getUniqueID())) {
		//	sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
		//	return;
		//}

		if (args.length == 0) {
			Plot plot = PlotRegistry.INSTANCE.getPlot(getCommandSenderAsPlayer(sender).getUniqueID());
			if (plot == null) {
				sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have a plot to teleport to."));
			} else plot.teleportToPlot(getCommandSenderAsPlayer(sender));

		} else if (args.length == 1) {
			try {
				int plotID = Integer.parseInt(args[0]);
				Plot plot = PlotRegistry.INSTANCE.getPlot(plotID);

				if (plot == null) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + "Plot id [" + plotID + "] does not exist."));
				} else plot.teleportToPlot(getCommandSenderAsPlayer(sender));
			} catch (NumberFormatException e) {

				EntityPlayerMP player = getPlayer(server, sender, args[0]);

				Plot plot = PlotRegistry.INSTANCE.getPlot(player.getUniqueID());
				if (plot == null) {
					sender.sendMessage(new TextComponentString(TextFormatting.RED + player.getName() + " does not have a plot."));
				} else plot.teleportToPlot(getCommandSenderAsPlayer(sender));
			}
		}
	}

	@NotNull
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return args.length >= 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
	}
}
