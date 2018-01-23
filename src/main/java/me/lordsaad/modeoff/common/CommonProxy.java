package me.lordsaad.modeoff.common;

import me.lordsaad.modeoff.ModItems;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.capability.DefaultModoffCapability;
import me.lordsaad.modeoff.api.capability.IModoffCapability;
import me.lordsaad.modeoff.api.capability.ModoffCapabilityStorage;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.api.world.ModOffWorldCapability;
import me.lordsaad.modeoff.client.gui.GuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.io.File;

import static me.lordsaad.modeoff.server.ServerProxy.fetchRanks;

/**
 * Created by LordSaad.
 */
public class CommonProxy {

	@Nullable
	public static File directory = null;

	public void preInit(FMLPreInitializationEvent event) {
		ModOffWorldCapability.register();

		ModItems.init();
		CapabilityManager.INSTANCE.register(IModoffCapability.class, new ModoffCapabilityStorage(), DefaultModoffCapability.class);

		MinecraftForge.EVENT_BUS.register(new EventHandler());
		MinecraftForge.EVENT_BUS.register(RankRegistry.INSTANCE);
	}

	public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(ModeratorOff.instance, new GuiHandler());
	}

	public void postInit(FMLPostInitializationEvent event) {
		PlotRegistry.INSTANCE.setDirectory(directory);
		PlotRegistry.INSTANCE.loadPlots();
		fetchRanks();
	}
}
