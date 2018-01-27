package me.lordsaad.modeoff.api.capability;

import me.lordsaad.modeoff.ModeratorOff;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Created by Saad on 8/16/2016.
 */
@Mod.EventBusSubscriber(modid = ModeratorOff.MOD_ID)
public final class ModoffCapabilityProvider implements ICapabilityProvider, INBTSerializable<NBTBase> {

	private static final ResourceLocation CAPABILITY_ID = new ResourceLocation(ModeratorOff.MOD_ID, "capabilities");

	@CapabilityInject(IModoffCapability.class)
	private static final Capability<IModoffCapability> CAPABILITY = null;

	private final IModoffCapability capability;

	public ModoffCapabilityProvider() {
		capability = new DefaultModoffCapability();
	}

	public ModoffCapabilityProvider(IModoffCapability capability) {
		this.capability = capability;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == capability();
	}

	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		return capability == capability() ? capability().cast(this.capability) : null;
	}

	@Override
	public NBTBase serializeNBT() {
		return capability().writeNBT(capability, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		capability().readNBT(capability, null, nbt);
	}

	@Nullable
	public static IModoffCapability getCap(Entity entity) {
		return entity.getCapability(CAPABILITY, null);
	}

	public static Capability<IModoffCapability> capability() {
		//noinspection ConstantConditions
		return Objects.requireNonNull(CAPABILITY, "CAPABILITY");
	}

	public static void init() {
		CapabilityManager.INSTANCE.register(IModoffCapability.class, new ModoffCapabilityStorage(), DefaultModoffCapability::new);
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> e) {
		if (e.getObject() instanceof EntityPlayer) {
			e.addCapability(CAPABILITY_ID, new ModoffCapabilityProvider(new DefaultModoffCapability()));
		}
	}
}
