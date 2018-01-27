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
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

public class GuiPlot extends GuiBase {

	static final Sprite LOCKED = new Sprite(new ResourceLocation(ModeratorOff.MOD_ID, "textures/gui/locked.png"));
	static final Sprite UNLOCKED = new Sprite(new ResourceLocation(ModeratorOff.MOD_ID, "textures/gui/unlocked.png"));
	static final Sprite CHECKBOX_CHECKED = new Sprite(new ResourceLocation(ModeratorOff.MOD_ID, "textures/gui/checkbox_checked.png"));
	static final Sprite CHECKBOX_XED = new Sprite(new ResourceLocation(ModeratorOff.MOD_ID, "textures/gui/checkbox_xed.png"));
	static final Sprite CHECKBOX_RADIO = new Sprite(new ResourceLocation(ModeratorOff.MOD_ID, "textures/gui/checkbox_radio.png"));
	private static String tag;
	//private EnumMap<BlockRenderLayer, HashMultimap<Icacher.blockstate, BlockPos>> cacher.blocks = new EnumMap<>(BlockRenderLayer.class);
	private HashSet<BlockPos> tempPosCache = new HashSet<>();
	private EnumMap<BlockRenderLayer, int[]> vboCaches = new EnumMap<>(BlockRenderLayer.class);

	private int tick;

	public GuiPlot() {
		super(400, 400);

		ComponentRect compRect = new ComponentRect(0, 0, getGuiWidth(), getGuiHeight());
		compRect.getColor().setValue(new Color(0x99FFFFFF, true));
		getMainComponents().add(compRect);

		Plot plot = PlotRegistry.INSTANCE.getPlot(Minecraft.getMinecraft().player.getUniqueID());
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

		Permission leftClickingPerm = PermissionRegistry.DefaultPermissions.PERMISSION_ENABLE_BLOCK_LEFT_CLICKING;
		new ComponentButton(20, 20 + (++id * 20) + (id * buffer), compRect, "Enable Block Left Clicking", plot.hasPermission(leftClickingPerm) ? CHECKBOX_CHECKED : CHECKBOX_XED, (componentSprite, componentText) -> {
			boolean hasPerm = plot.hasPermission(leftClickingPerm);

			if (hasPerm) {
				plot.removePermission(leftClickingPerm);
				componentSprite.setSprite(CHECKBOX_XED);
			} else {
				plot.addPermission(leftClickingPerm);
				componentSprite.setSprite(CHECKBOX_CHECKED);
			}

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
			Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, hasPerm ? 2f : 1f);
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If enabled, the left click block event will be cancelled to the viewers (your audience)");
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

			} else {
				plot.removePermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_SPECTATOR);
				plot.addPermission(PermissionRegistry.DefaultPermissions.PERMISSION_GAMEMODE_ADVENTURE);
				newName = "Adventure";
			}

			componentText.getText().setValue("Gamemode " + newName);

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
			Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, 1f);
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If enabled, the left click block event will be cancelled to the viewers (your audience)");
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
			txt.add("If enabled, the left click block event will be cancelled to the viewers (your audience)");
			return txt;
		});

		/*{
			PlotDimensions dimensions = plot.getDimensions();
			BlockPos plotPos = new BlockPos(plot.getPlotPos().getXi(), ConfigValues.y, plot.getPlotPos().getYi());
			BlockPos min = dimensions.getCorner1();
			BlockPos max = dimensions.getCorner2();

			ChunkCache blockAccess = new PlotChunkCache(Minecraft.getMinecraft().world, min.subtract(new Vec3i(3, 3, 3)), max.add(3, 3, 3), 0);

			PlotCacher cacher = new PlotCacher(blockAccess, plot);

			for (BlockRenderLayer layer : cacher.blocks.keySet()) {
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buffer0 = tes.getBuffer();
				BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

				if (vboCaches.get(layer) == null) {

					buffer0.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

					for (IBlockState state : cacher.blocks.get(layer).keySet()) {
						for (BlockPos pos : cacher.blocks.get(layer).get(state)) {
							BlockPos origin = pos.subtract(plotPos);
							buffer0.setTranslation(-pos.getX() + origin.getX(), -pos.getY() + origin.getY(), -pos.getZ() + origin.getZ());
							dispatcher.renderBlock(state, pos, blockAccess, buffer0);
							buffer0.setTranslation(0, 0, 0);
						}
					}

					vboCaches.put(layer, ClientUtilMethods.createCacheArrayAndReset(buffer0));
				}
			}

			// RENDER IT

			ComponentVoid sideView = new ComponentVoid(0, getGuiHeight() - 200, getGuiWidth(), 200);
			getMainComponents().add(sideView);

			ScissorMixin.INSTANCE.scissor(sideView);

			sideView.BUS.hook(GuiComponentEvents.PostDrawEvent.class, (event) -> {

				if (tick > 360) tick = 0;
				else tick++;

				int horizontalAngle = 25;
				int verticalAngle = 45;

				GlStateManager.pushMatrix();
				GlStateManager.enableCull();

				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GlStateManager.shadeModel(GL11.GL_SMOOTH);
				GlStateManager.translate(
						(sideView.getSize().getX() / 2.0) - ((ConfigValues.plotSize) * 0.8 / 2.0),
						(sideView.getSize().getY() / 2.0) - ((ConfigValues.plotSize) * 0.8 / 2.0),
						500);
				GlStateManager.rotate(horizontalAngle, -1, 0, 0);
				GlStateManager.rotate((float) (tick), 0, 1, 0);
				GlStateManager.scale(4, -4, 4);
				GlStateManager.translate(0.5, 0.5, 0.5);

				mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
				for (BlockRenderLayer layer : cacher.blocks.keySet()) {
					Tessellator tes = Tessellator.getInstance();
					BufferBuilder buffer1 = tes.getBuffer();

					buffer1.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
					buffer1.addVertexData(vboCaches.get(layer));
					tes.draw();
				}

				GlStateManager.disableCull();
				GlStateManager.popMatrix();
			});
		}*/
		// SIDE VIEW //
	}
}
