package me.lordsaad.modeoff.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Created by LordSaad.
 */
public class CommandManager extends CommandBase {

	@NotNull
	@Override
	public String getName() {
		return "plot_manager";
	}

	@NotNull
	@Override
	public String getUsage(@NotNull ICommandSender sender) {
		return "/plot_manager [username]";
	}

	@Override
	public void execute(@NotNull MinecraftServer server, @NotNull ICommandSender sender, @NotNull String[] args) throws CommandException {
		//if (sender instanceof EntityPlayer)
		//	if (!CommonProxy.teamMembers.contains(((EntityPlayer) sender).getUniqueID()) || !CommonProxy.contestants.contains(((EntityPlayer) sender).getUniqueID())) {
		//		sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
		//		return;
		//	}
		EntityPlayer player;
		if (sender instanceof EntityPlayer) player = getCommandSenderAsPlayer(sender);
		else throw new WrongUsageException(getUsage(sender));

		//PacketHandler.NETWORK.sendTo(new PacketManagerGui(), (EntityPlayerMP) player);
	}

	@NotNull
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : Collections.emptyList();
	}
}
