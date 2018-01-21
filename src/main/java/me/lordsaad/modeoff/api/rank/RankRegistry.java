package me.lordsaad.modeoff.api.rank;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import me.lordsaad.modeoff.api.rank.defaultranks.RankAdmin;
import me.lordsaad.modeoff.api.rank.defaultranks.RankJudge;
import me.lordsaad.modeoff.api.rank.defaultranks.RankNormal;
import me.lordsaad.modeoff.api.rank.defaultranks.RankSponsor;
import net.minecraft.entity.player.EntityPlayer;

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
		return true;
	}
}
