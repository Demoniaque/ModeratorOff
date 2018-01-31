package me.lordsaad.modeoff.server;

import com.mojang.authlib.GameProfile;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.ConfigValues;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.IRank;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.common.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class ServerProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);

		File configFolder = new File(event.getModConfigurationDirectory(), "/plots/");

		if (!configFolder.exists()) {
			ModeratorOff.logger.info(configFolder.getName() + " not found. Creating directory...");
			if (!configFolder.mkdirs()) {
				ModeratorOff.logger.error("SOMETHING WENT WRONG! Could not create config directory " + configFolder.getName());
				return;
			}
			ModeratorOff.logger.info(configFolder.getName() + " has been created successfully!");
		}

		directory = configFolder;

		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);

		if (ConfigValues.enablePlotsAndRanks) {
			PlotRegistry.INSTANCE.setDirectory(directory);
			PlotRegistry.INSTANCE.loadPlots();
			fetchRanks();
		}

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
