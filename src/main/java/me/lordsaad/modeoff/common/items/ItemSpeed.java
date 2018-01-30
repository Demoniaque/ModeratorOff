package me.lordsaad.modeoff.common.items;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import me.lordsaad.modeoff.api.capability.IModoffCapability;
import me.lordsaad.modeoff.api.capability.ModoffCapabilityProvider;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import me.lordsaad.modeoff.api.plot.Plot;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
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


		if (worldIn.isRemote) {
			playerIn.sendMessage(new TextComponentString(TextFormatting.GRAY + "Speed set to [" + TextFormatting.GREEN + (level + 1) + TextFormatting.GRAY + "]"));
			playerIn.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, 1f + level / 4f);
		}

		if (playerIn instanceof EntityPlayerMP) {

			PlayerCapabilities capabilities = playerIn.capabilities;

			capabilities.allowFlying = true;
			capabilities.allowEdit = false;

			IModoffCapability cap = ModoffCapabilityProvider.getCap(playerIn);
			if (cap != null) {
				Plot plot = cap.getEnclosingPlot();
				if (plot != null) {

					capabilities.allowFlying = !plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_DISABLE_FLIGHT);
					capabilities.allowEdit = !plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_BREAKING);
				}
			}

			SPacketPlayerAbilities packet = new SPacketPlayerAbilities(capabilities);
			packet.setFlySpeed(0.05f * (level + 1));

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

		return TextFormatting.GRAY + "Increase Speed [" + TextFormatting.GREEN + "firstPlotCenterX" + (level + 1) + TextFormatting.GRAY + "]";
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add("Increase your fly and walk speed.");
		tooltip.add("Right click to change speed.");
	}
}
