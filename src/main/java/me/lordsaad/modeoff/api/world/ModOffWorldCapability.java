package me.lordsaad.modeoff.api.world;

import java.util.Objects;

import me.lordsaad.modeoff.ModeratorOff;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ModeratorOff.MOD_ID)
public final class ModOffWorldCapability {
	private ModOffWorldCapability() {}

	private static final ResourceLocation MODOFF_WORLD_ID = new ResourceLocation(ModeratorOff.MOD_ID, "modoff_world");

	@CapabilityInject(ModOffWorld.class)
	private static final Capability<ModOffWorld> CAPABILITY = null;

	public static Capability<ModOffWorld> capability() {
		//noinspection ConstantConditions
		return Objects.requireNonNull(CAPABILITY, "CAPABILITY");
	}

	public static void register() {
		CapabilityManager.INSTANCE.register(ModOffWorld.class, new Storage(), StandardModOffWorld::new);
	}

	public static ModOffWorld get(World world) {
		ModOffWorld cap = world.getCapability(capability(), null);
		if (cap == null) {
			throw new IllegalStateException("Missing capability: " + world.getWorldInfo().getWorldName() + "/" + world.provider.getDimensionType().getName());
		}
		return cap;
	}

	@SubscribeEvent
	public static void onAttachCapabilities(AttachCapabilitiesEvent<World> event) {
		event.addCapability(MODOFF_WORLD_ID, new StandardModOffWorld());
	}

	private static final class Storage implements Capability.IStorage<ModOffWorld> {
		@Override
		public NBTBase writeNBT(Capability<ModOffWorld> capability, ModOffWorld instance, EnumFacing side) {
			return new NBTTagCompound();
		}

		@Override
		public void readNBT(Capability<ModOffWorld> capability, ModOffWorld instance, EnumFacing side, NBTBase nbt) {}
	}
}
