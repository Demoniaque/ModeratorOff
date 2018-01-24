package me.lordsaad.modeoff.api.rank;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.capability.DefaultModoffCapability;
import me.lordsaad.modeoff.api.capability.IModoffCapability;
import me.lordsaad.modeoff.api.capability.ModoffCapabilityProvider;
import me.lordsaad.modeoff.api.rank.defaultranks.RankAdmin;
import me.lordsaad.modeoff.api.rank.defaultranks.RankJudge;
import me.lordsaad.modeoff.api.rank.defaultranks.RankNormal;
import me.lordsaad.modeoff.api.rank.defaultranks.RankSponsor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class RankRegistry {
	public static RankRegistry INSTANCE = new RankRegistry();

	public HashBiMap<Integer, IRank> ranks = HashBiMap.create();
	public HashMultimap<IRank, UUID> rankMap = HashMultimap.create();

	private RankRegistry() {
		int id = 0;
		ranks.put(id++, new RankAdmin());
		ranks.put(id++, new RankNormal());
		ranks.put(id++, new RankJudge());
		ranks.put(id, new RankSponsor());
	}

	public boolean isAdmin(EntityPlayer player) {
		return getPermission(player) == EnumPerm.ADMIN;
	}

	public EnumPerm getPermission(EntityPlayer player) {
		IModoffCapability cap = ModoffCapabilityProvider.getCap(player);
		return cap == null ? EnumPerm.NONE : cap.getRank().getPermission();
	}

	@Nullable
	public IRank getRank(EntityPlayer player) {
		IModoffCapability cap = ModoffCapabilityProvider.getCap(player);
		return cap == null ? null : cap.getRank();
	}

	@SubscribeEvent
	public void onAddCapabilities(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof EntityPlayer) {
			ModoffCapabilityProvider provider = new ModoffCapabilityProvider(new DefaultModoffCapability());
			e.addCapability(new ResourceLocation(ModeratorOff.MOD_ID, "capabilities"), provider);
		}
	}

	@SubscribeEvent
	public void joinWorld(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityPlayer) {

			IModoffCapability cap = ModoffCapabilityProvider.getCap(event.getEntity());
			if (cap == null) return;

			rankMap.forEach((iRank, uuid) -> {
				if (uuid.equals(event.getEntity().getUniqueID())) {
					if (cap.getRank() == iRank) return;
					cap.setRank(iRank);
					cap.dataChanged(event.getEntity());
				}
			});
		}
	}
}
