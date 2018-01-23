package me.lordsaad.modeoff.api.capability;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Saad on 8/16/2016.
 */
public class ModoffCapabilityProvider implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

	@CapabilityInject(IModoffCapability.class)
	public static final Capability<IModoffCapability> modoffCapability = null;
	private final IModoffCapability capability;

	public ModoffCapabilityProvider() {
		capability = new DefaultModoffCapability();
	}

	public ModoffCapabilityProvider(IModoffCapability capability) {
		this.capability = capability;
	}

	@Nullable
	public static IModoffCapability getCap(Entity entity) {
		return entity.getCapability(modoffCapability, null);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == modoffCapability;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		if ((modoffCapability != null) && (capability == modoffCapability)) return (T) this.capability;
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		return capability.saveNBTData();
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		capability.loadNBTData(nbt);
	}

}
