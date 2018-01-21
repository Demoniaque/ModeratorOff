package me.lordsaad.modeoff.server;

import com.teamwizardry.librarianlib.features.network.PacketHandler;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.ConfigValues;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import me.lordsaad.modeoff.common.network.PacketSyncPlots;
import me.lordsaad.modeoff.common.network.PacketSyncRanks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by LordSaad.
 */
public class ServerEventHandler {

	@SubscribeEvent
	public void onWorldLoad(EntityJoinWorldEvent event) {
		if (ConfigValues.enablePlotsAndRanks)
			if (event.getEntity() instanceof EntityPlayerMP) {
				ModeratorOff.logger.info("About to sync data to " + event.getEntity().getName());
				PacketHandler.NETWORK.sendTo(new PacketSyncPlots(PlotRegistry.INSTANCE.plots), (EntityPlayerMP) event.getEntity());
				PacketHandler.NETWORK.sendTo(new PacketSyncRanks(RankRegistry.INSTANCE.rankMap), (EntityPlayerMP) event.getEntity());
				ModeratorOff.logger.info("Done.");
			}
	}
}
