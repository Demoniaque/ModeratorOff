package me.lordsaad.modeoff.client.gui;

import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.permissions.Permission;
import me.lordsaad.modeoff.api.permissions.PermissionRegistry;
import me.lordsaad.modeoff.api.permissions.defaultperms.PermissionGamemode;
import me.lordsaad.modeoff.api.permissions.defaultperms.PermissionGamemodeCreative;
import me.lordsaad.modeoff.api.permissions.defaultperms.PermissionGamemodeSpectator;
import me.lordsaad.modeoff.api.permissions.defaultperms.PermissionGamemodeSurvival;
import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.common.network.PacketUpdatePlot;
import net.minecraft.client.Minecraft;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GuiPlot extends GuiBase {

	private static final Sprite CHECKBOX_CHECKED = new Sprite(new ResourceLocation(ModeratorOff.MOD_ID, "textures/gui/checkbox_checked.png"));
	private static final Sprite CHECKBOX_XED = new Sprite(new ResourceLocation(ModeratorOff.MOD_ID, "textures/gui/checkbox_xed.png"));
	private static final Sprite CHECKBOX_RADIO = new Sprite(new ResourceLocation(ModeratorOff.MOD_ID, "textures/gui/checkbox_radio.png"));

	public GuiPlot(int plotID) {
		super(400, 400);

		ComponentRect compRect = new ComponentRect(0, 0, getGuiWidth(), getGuiHeight());
		compRect.getColor().setValue(new Color(0x99FFFFFF, true));
		getMainComponents().add(compRect);

		Plot plot;
		if (plotID == -1)
			plot = PlotRegistry.INSTANCE.getPlot(Minecraft.getMinecraft().player.getUniqueID());
		else plot = PlotRegistry.INSTANCE.getPlot(plotID);

		if (plot == null) return;
		if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) return;

		int buffer = 20;
		int id = 0;
		Permission lockPlotPerm = PermissionRegistry.DefaultPermissions.PERMISSION_LOCK_PLOT;
		new ComponentButton(20, 20 + (id * 20) + (id * buffer), compRect, "Lock Plot", plot.hasPermission(lockPlotPerm) ? CHECKBOX_CHECKED : CHECKBOX_XED, (componentSprite, componentText) -> {
			boolean hasPerm = plot.hasPermission(lockPlotPerm);

			if (hasPerm) {
				plot.removePermission(lockPlotPerm);
				componentSprite.setSprite(CHECKBOX_XED);
			} else {
				plot.addPermission(lockPlotPerm);
				componentSprite.setSprite(CHECKBOX_CHECKED);
			}

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
			Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, hasPerm ? 2f : 1f);

		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If a plot is locked, no one including the plot owners (yourself) will be able to edit the plot.");
			return txt;
		});

		Permission blockPlacingPerm = PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_PLACING;
		new ComponentButton(20, 20 + (++id * 20) + (id * buffer), compRect, "Enable Block Placing", plot.hasPermission(blockPlacingPerm) ? CHECKBOX_CHECKED : CHECKBOX_XED, (componentSprite, componentText) -> {
			boolean hasPerm = plot.hasPermission(blockPlacingPerm);

			if (hasPerm) {
				plot.removePermission(blockPlacingPerm);
				componentSprite.setSprite(CHECKBOX_XED);
			} else {
				plot.addPermission(blockPlacingPerm);
				componentSprite.setSprite(CHECKBOX_CHECKED);
			}

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
			Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, hasPerm ? 2f : 1f);
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If enabled, viewers of the plot (your audience) will be able to place cacher.blocks on the plot");
			return txt;
		});

		Permission blockBreakingPerm = PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_BREAKING;
		new ComponentButton(20, 20 + (++id * 20) + (id * buffer), compRect, "Enable Block Breaking", plot.hasPermission(blockBreakingPerm) ? CHECKBOX_CHECKED : CHECKBOX_XED, (componentSprite, componentText) -> {
			boolean hasPerm = plot.hasPermission(blockBreakingPerm);

			if (hasPerm) {
				plot.removePermission(blockBreakingPerm);
				componentSprite.setSprite(CHECKBOX_XED);
			} else {
				plot.addPermission(blockBreakingPerm);
				componentSprite.setSprite(CHECKBOX_CHECKED);
			}

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
			Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, hasPerm ? 2f : 1f);
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If enabled, viewers of the plot (your audience) will be able to break blocks on the plot");
			return txt;
		});

		Permission flightPerm = PermissionRegistry.DefaultPermissions.PERMISSION_DISABLE_FLIGHT;
		new ComponentButton(20, 20 + (++id * 20) + (id * buffer), compRect, "Disable Flight", plot.hasPermission(flightPerm) ? CHECKBOX_CHECKED : CHECKBOX_XED, (componentSprite, componentText) -> {
			boolean hasPerm = plot.hasPermission(flightPerm);

			if (hasPerm) {
				plot.removePermission(flightPerm);
				componentSprite.setSprite(CHECKBOX_XED);
			} else {
				plot.addPermission(flightPerm);
				componentSprite.setSprite(CHECKBOX_CHECKED);
			}

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
			Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, hasPerm ? 2f : 1f);
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If enabled, your audience will not be able to fly");
			return txt;
		});

		PermissionGamemode gamemodePerm = PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_ADVENTURE;
		for (Permission permission : plot.getPermissions()) {
			if (permission instanceof PermissionGamemode) {
				gamemodePerm = (PermissionGamemode) permission;
			}
		}

		String name = "Adventure";
		if (gamemodePerm instanceof PermissionGamemodeSurvival) {
			name = "Survival";
		} else if (gamemodePerm instanceof PermissionGamemodeSpectator) {
			name = "Spectator";
		} else if (gamemodePerm instanceof PermissionGamemodeCreative) {
			name = "Creative";
		}

		new ComponentButton(20, 20 + (++id * 20) + (id * buffer), compRect, "Gamemode " + name, CHECKBOX_RADIO, (componentSprite, componentText) -> {

			String newName;

			if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_ADVENTURE)) {
				plot.removePermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_ADVENTURE);
				plot.addPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_CREATIVE);
				newName = "Creative";

			} else if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_CREATIVE)) {
				plot.removePermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_CREATIVE);
				plot.addPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_SURVIVAL);
				newName = "Surival";

			} else if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_SURVIVAL)) {
				plot.removePermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_SURVIVAL);
				plot.addPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_SPECTATOR);
				newName = "Spectator";

			} else if (plot.hasPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_SPECTATOR)) {
				plot.removePermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_SPECTATOR);
				plot.addPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_ADVENTURE);
				newName = "Adventure";
			} else {
				plot.removePermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_ADVENTURE);
				plot.addPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_CREATIVE);
				newName = "Creative";
			}

			componentText.getText().setValue("Gamemode " + newName);

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
			Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, 1f);
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("Change your audience's gamemode when they enter the plot");
			return txt;
		});
	}
}
