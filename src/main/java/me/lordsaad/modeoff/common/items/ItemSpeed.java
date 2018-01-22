package me.lordsaad.modeoff.common.items;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSpeed extends ItemMod {

	public ItemSpeed() {
		super("speed");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);

		NBTTagCompound compound;
		if (stack.getTagCompound() == null) compound = new NBTTagCompound();
		else compound = stack.getTagCompound();

		int level;
		if (!compound.hasKey("level")) {
			compound.setInteger("level", level = 0);
		} else {
			level = compound.getInteger("level");

			if (level >= 4) level = 0;
			else if (level < 0) level = 3;
			else level++;

			compound.setInteger("level", level);
		}

		stack.setTagCompound(compound);


		playerIn.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, 1f + level / 4f);
		if (playerIn instanceof EntityPlayerMP) {
			SPacketPlayerAbilities packet = new SPacketPlayerAbilities();
			packet.setFlySpeed(0.05f * (level + 1));
			packet.setFlying(playerIn.capabilities.isFlying);
			packet.setCreativeMode(playerIn.capabilities.isCreativeMode);
			packet.setAllowFlying(playerIn.capabilities.allowFlying);

			((EntityPlayerMP) playerIn).connection.sendPacket(packet);

			if (level == 0) playerIn.removePotionEffect(MobEffects.SPEED);
			else playerIn.addPotionEffect(new PotionEffect(MobEffects.SPEED, 999999, level * 10, true, false));

		} else {
			int finalLevel = level;
			ClientRunnable.run(new ClientRunnable() {
				@Override
				@SideOnly(Side.CLIENT)
				public void runIfClient() {
					playerIn.capabilities.setFlySpeed(0.05f * (finalLevel + 1));
				}
			});
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@NotNull
	@Override
	public String getItemStackDisplayName(@NotNull ItemStack stack) {
		NBTTagCompound compound;
		if (stack.getTagCompound() == null) compound = new NBTTagCompound();
		else compound = stack.getTagCompound();

		int level;
		if (!compound.hasKey("level")) {
			level = 0;
		} else {
			level = compound.getInteger("level");
		}

		return "Increase Speed [x" + (level + 1) + "]";
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add("Increase your fly and walk speed.");
	}
}
