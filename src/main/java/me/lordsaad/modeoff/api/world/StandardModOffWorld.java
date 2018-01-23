package me.lordsaad.modeoff.api.world;

import javax.annotation.Nullable;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public final class StandardModOffWorld implements ModOffWorld {
	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == ModOffWorldCapability.capability();
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		return hasCapability(capability, facing) ? ModOffWorldCapability.capability().cast(this) : null;
	}
}
