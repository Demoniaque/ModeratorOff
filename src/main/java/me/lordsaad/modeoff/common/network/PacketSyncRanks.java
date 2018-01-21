package me.lordsaad.modeoff.common.network;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.autoregister.PacketRegister;
import com.teamwizardry.librarianlib.features.network.PacketBase;
import com.teamwizardry.librarianlib.features.saving.SaveMethodGetter;
import com.teamwizardry.librarianlib.features.saving.SaveMethodSetter;
import me.lordsaad.modeoff.api.rank.IRank;
import me.lordsaad.modeoff.api.rank.RankRegistry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

@PacketRegister(Side.CLIENT)
public class PacketSyncRanks extends PacketBase {

	private HashMultimap<IRank, UUID> rankMap;

	public PacketSyncRanks() {
	}

	public PacketSyncRanks(HashMultimap<IRank, UUID> rankMap) {
		this.rankMap = rankMap;
	}

	@SaveMethodSetter(saveName = "manual_saver")
	private void manualSaveSetter(NBTTagCompound compound) {
		rankMap = HashMultimap.create();
		if (compound == null) return;

		for (Integer rankID : RankRegistry.INSTANCE.ranks.keySet()) {
			NBTTagList list = compound.getTagList(rankID + "", Constants.NBT.TAG_STRING);
			for (int i = 0; i < list.tagCount(); i++) {
				UUID uuid = UUID.fromString(list.getStringTagAt(i));
				RankRegistry.INSTANCE.rankMap.put(RankRegistry.INSTANCE.ranks.get(rankID), uuid);
			}
		}
	}

	@SaveMethodGetter(saveName = "manual_saver")
	private NBTTagCompound manualSaveGetter() {
		NBTTagCompound nbt = new NBTTagCompound();

		if (rankMap == null) return nbt;

		for (IRank rank : rankMap.keySet()) {
			NBTTagList list = new NBTTagList();

			for (UUID uuid : rankMap.get(rank)) {
				list.appendTag(new NBTTagString(uuid.toString()));
			}

			nbt.setTag(RankRegistry.INSTANCE.ranks.inverse().get(rank) + "", list);
		}
		return nbt;
	}

	@Override
	public void handle(MessageContext messageContext) {
		RankRegistry.INSTANCE.rankMap = rankMap;
	}
}
