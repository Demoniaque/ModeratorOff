package me.lordsaad.modeoff.common.items;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import me.lordsaad.modeoff.api.ConfigValues;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

public class ItemTeleport extends ItemMod {

	public ItemTeleport() {
		super("teleport");
	}

	@NotNull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @NotNull EnumHand handIn) {
		ItemStack stack = playerIn.getHeldItem(handIn);

		NBTTagCompound compound;
		if (stack.getTagCompound() == null) compound = new NBTTagCompound();
		else compound = stack.getTagCompound();

		String type;
		if (!compound.hasKey("type")) compound.setString("type", "plots");

		type = compound.getString("type");

		if (playerIn.isSneaking()) {
			if (type.equalsIgnoreCase("spawn")) {
				type = "plots";
			} else type = "spawn";

			if (worldIn.isRemote) {
				playerIn.sendMessage(new TextComponentString(TextFormatting.GRAY + "Teleport set to [" + TextFormatting.GREEN + type + TextFormatting.GRAY + "]"));
				playerIn.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, 1);
			}

			compound.setString("type", type);
			stack.setTagCompound(compound);

			return super.onItemRightClick(worldIn, playerIn, handIn);
		}

		if (!worldIn.isRemote) {
			if (playerIn.isRiding()) playerIn.dismountRidingEntity();
			if (playerIn.isBeingRidden()) playerIn.getPassengers().forEach(Entity::dismountRidingEntity);
			if (playerIn.dimension != 0) playerIn.changeDimension(0);

			if (type.equalsIgnoreCase("spawn"))
				playerIn.setPositionAndUpdate(ConfigValues.spawnX, ConfigValues.spawnY, ConfigValues.spawnZ);
			else playerIn.setPositionAndUpdate(ConfigValues.plotX, ConfigValues.plotY, ConfigValues.plotZ);

			playerIn.getCooldownTracker().setCooldown(this, 20);
		} else {
			playerIn.sendMessage(new TextComponentString(TextFormatting.GRAY + "Teleporting to [" + TextFormatting.GREEN + type + TextFormatting.GRAY + "]"));
			playerIn.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, 3f);
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@NotNull
	@Override
	public String getItemStackDisplayName(@NotNull ItemStack stack) {
		NBTTagCompound compound;
		if (stack.getTagCompound() == null) compound = new NBTTagCompound();
		else compound = stack.getTagCompound();

		String type;
		if (!compound.hasKey("type")) {
			type = "plots";
		} else {
			type = compound.getString("type");
		}

		return TextFormatting.GRAY + "Teleport to the [" + TextFormatting.GREEN + type + TextFormatting.GRAY + "]";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		tooltip.add("Teleport to either the spawn or plot world.");
		tooltip.add("Right click to teleport.");
		tooltip.add("Shift + right click to change teleport location.");
	}
}
