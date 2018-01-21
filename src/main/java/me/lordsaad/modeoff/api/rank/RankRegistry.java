package me.lordsaad.modeoff.api.rank;

import com.google.common.collect.HashMultimap;
import me.lordsaad.modeoff.api.rank.defaultranks.RankAdmin;
import me.lordsaad.modeoff.api.rank.defaultranks.RankJudge;
import me.lordsaad.modeoff.api.rank.defaultranks.RankNormal;
import me.lordsaad.modeoff.api.rank.defaultranks.RankSponsor;
import net.minecraft.entity.player.EntityPlayer;

import java.io.File;
import java.util.HashSet;
import java.util.UUID;

/**
 * Created by LordSaad.
 */
public class RankRegistry {
	public static RankRegistry INSTANCE = new RankRegistry();

	public HashSet<IRank> ranks = new HashSet<>();
	public HashMultimap<IRank, UUID> rankMap = HashMultimap.create();

	public File directory, config;

	private RankRegistry() {
		ranks.add(new RankAdmin());
		ranks.add(new RankNormal());
		ranks.add(new RankJudge());
		ranks.add(new RankSponsor());
	}

	public boolean isAdmin(EntityPlayer player) {
		return true;
	}

	public void initRanks() {

	}
}
