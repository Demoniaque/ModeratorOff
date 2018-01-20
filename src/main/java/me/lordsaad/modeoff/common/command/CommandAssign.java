package me.lordsaad.modeoff.common.command;

import me.lordsaad.modeoff.api.Plot;
import me.lordsaad.modeoff.api.PlotRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class CommandAssign extends CommandBase {

	@NotNull
	@Override
	public String getName() {
		return "plot_assign";
	}

	@NotNull
	@Override
	public String getUsage(@NotNull ICommandSender sender) {
		return "/plot_assign [username]";
	}

	@Override
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) throws CommandException {
		//if (sender instanceof EntityPlayer)
		//	if (!CommonProxy.teamMembers.contains(((EntityPlayer) sender).getUniqueID()) || !CommonProxy.contestants.contains(((EntityPlayer) sender).getUniqueID())) {
		//		sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
		//		return;
		//	}
		EntityPlayer player = null;
		//if (args.length >= 1) {
		//	if ((sender instanceof EntityPlayer) && CommonProxy.teamMembers.contains(((EntityPlayer) sender).getUniqueID()))
		//		player = getPlayer(server, sender, args[0]);
		//} else if (sender instanceof EntityPlayer) player = getCommandSenderAsPlayer(sender);
		//else throw new WrongUsageException(getUsage(sender));

		player = getCommandSenderAsPlayer(sender);

		//if (player == null) throw new WrongUsageException(getUsage(sender));

		//if (PlotRegistry.INSTANCE.isUUIDRegistered(player.getUniqueID())) {
		//	CommonProxy.contestants.add(player.getUniqueID());
		//	sender.sendMessage(new TextComponentString(TextFormatting.RED + "The plot for '" + TextFormatting.GOLD + player.getName() + TextFormatting.RED + "' has already been registered. Do /plot_tp to teleport to it."));
		//	return;
		//}

		HashSet<UUID> owners = new HashSet<>();
		owners.add(player.getUniqueID());

		Plot plot = PlotRegistry.INSTANCE.registerPlot(new Plot(Integer.parseInt(args[0]), owners));

		plot.teleportToPlot(player);
		sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "A plot has been assigned for '" + TextFormatting.GOLD + player.getName() + TextFormatting.GREEN + "' successfully! Plot ID: " + plot.getID()));
		if (!sender.getName().equals(player.getName()))
			player.sendMessage(new TextComponentString(TextFormatting.GREEN + "A plot has been assigned for '" + TextFormatting.GOLD + player.getName() + TextFormatting.GREEN + "' successfully! Plot ID: " + plot.getID()));
	}


	@NotNull
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
	}
}
