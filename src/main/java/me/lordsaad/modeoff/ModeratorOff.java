package me.lordsaad.modeoff;

import me.lordsaad.modeoff.common.CommonProxy;
import me.lordsaad.modeoff.common.command.CommandAssign;
import me.lordsaad.modeoff.common.command.CommandManager;
import me.lordsaad.modeoff.common.command.CommandRank;
import me.lordsaad.modeoff.common.command.CommandTpPlot;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;

@Mod(
		modid = ModeratorOff.MOD_ID,
		name = ModeratorOff.MOD_NAME,
		version = ModeratorOff.VERSION,
		dependencies = "required-after:librarianlib"
)
public class ModeratorOff {

	public static final String MOD_ID = "modeoff";
	public static final String MOD_NAME = "ModeratorOff";
	public static final String VERSION = "1.0";

	public static final String CLIENT = "me.lordsaad.modeoff.client.ClientProxy";
	public static final String SERVER = "me.lordsaad.modeoff.server.ServerProxy";

	public static Logger logger;

	@SidedProxy(clientSide = CLIENT, serverSide = SERVER)
	public static CommonProxy proxy;

	@Mod.Instance
	public static ModeratorOff instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandAssign());
		event.registerServerCommand(new CommandTpPlot());
		event.registerServerCommand(new CommandManager());
		event.registerServerCommand(new CommandRank());
	}
}
