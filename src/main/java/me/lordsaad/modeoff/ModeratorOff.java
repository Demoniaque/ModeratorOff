package me.lordsaad.modeoff;

import com.mojang.authlib.GameProfile;
import me.lordsaad.modeoff.api.ConfigValues;
import me.lordsaad.modeoff.api.rank.IRank;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.common.CommonProxy;
import me.lordsaad.modeoff.common.command.CommandPlot;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;

@Mod(
		modid = ModeratorOff.MOD_ID,
		name = ModeratorOff.MOD_NAME,
		version = ModeratorOff.VERSION,
		dependencies = "required-after:librarianlib"
)
public class ModeratorOff {

	public static final String MOD_ID = "modeoff";
	public static final String MOD_NAME = "ModeratorOff";
	public static final String VERSION = "1.2.1";

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
		event.registerServerCommand(new CommandPlot());
	}

	@Mod.EventHandler
	public void start(FMLServerStartedEvent event) {
		if (ConfigValues.enableWhiteList) {
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().setWhiteListEnabled(true);

			for (Map.Entry<IRank, UUID> entry : RankRegistry.INSTANCE.rankMap.entries()) {
				if (entry.getKey() == RankRegistry.DefaultRanks.NORMAL) continue;
				String name = CommonProxy.playerUUIDMap.inverse().get(entry.getValue());
				FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().addWhitelistedPlayer(new GameProfile(entry.getValue(), name));
			}
		} else {
			FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().setWhiteListEnabled(false);
		}
	}
}
