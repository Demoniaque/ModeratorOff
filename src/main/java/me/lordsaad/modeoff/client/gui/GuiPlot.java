package me.lordsaad.modeoff.client.gui;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.kotlin.ClientUtilMethods;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.AreaCacher;
import me.lordsaad.modeoff.api.ConfigValues;
import me.lordsaad.modeoff.api.PlotChunkCache;
import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.common.network.PacketUpdatePlot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import org.lwjgl.opengl.GL11;

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
	private static String tag;
	private EnumMap<BlockRenderLayer, HashMultimap<IBlockState, BlockPos>> blocks = new EnumMap<>(BlockRenderLayer.class);
	private HashSet<BlockPos> tempPosCache = new HashSet<>();
	private EnumMap<BlockRenderLayer, int[]> vboCaches = new EnumMap<>(BlockRenderLayer.class);

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
		tag = "lock_plot";
		new ComponentButton(20, 20 + (id * 20) + (id * buffer), compRect, (plot.hasTag(tag) ? "Unlock" : "Lock") + " Plot", plot.hasTag(tag) ? CHECKBOX_CHECKED : CHECKBOX_XED, (componentSprite, componentText) -> {
			if (plot.hasTag(tag)) {
				plot.removeTag(tag);
			} else plot.addTag(tag);

			componentText.getText().setValue((plot.hasTag(tag) ? "Unlock" : "Lock") + " Plot");
			if (plot.hasTag(tag)) {
				componentSprite.setSprite(CHECKBOX_CHECKED);
			} else componentSprite.setSprite(CHECKBOX_XED);

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If a plot is locked, no one including the plot owners (yourself) will be able to edit the plot.");
			return txt;
		});

		tag = "edit_plot_place";
		new ComponentButton(20, 20 + (++id * 20) + (id * buffer), compRect, (plot.hasTag(tag) ? "Enable" : "Disable") + " Block Placing", plot.hasTag(tag) ? CHECKBOX_CHECKED : CHECKBOX_XED, (componentSprite, componentText) -> {
			if (plot.hasTag(tag)) {
				plot.removeTag(tag);
			} else plot.addTag(tag);

			componentText.getText().setValue((plot.hasTag(tag) ? "Enable" : "Disable") + " Block Placing");
			if (plot.hasTag(tag)) {
				componentSprite.setSprite(CHECKBOX_CHECKED);
			} else componentSprite.setSprite(CHECKBOX_XED);

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If enabled, viewers of the plot (your audience) will be able to place blocks on the plot");
			return txt;
		});

		tag = "edit_plot_break";
		new ComponentButton(20, 20 + (++id * 20) + (id * buffer), compRect, (plot.hasTag(tag) ? "Enable" : "Disable") + " Block Breaking", plot.hasTag(tag) ? CHECKBOX_CHECKED : CHECKBOX_XED, (componentSprite, componentText) -> {
			if (plot.hasTag(tag)) {
				plot.removeTag(tag);
			} else plot.addTag(tag);

			componentText.getText().setValue((plot.hasTag(tag) ? "Enable" : "Disable") + " Block Breaking");
			if (plot.hasTag(tag)) {
				componentSprite.setSprite(CHECKBOX_CHECKED);
			} else componentSprite.setSprite(CHECKBOX_XED);

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If enabled, viewers of the plot (your audience) will be able to break blocks on the plot");
			return txt;
		});


		if (false) {

			ChunkCache blockAccess = new PlotChunkCache(Minecraft.getMinecraft().world, plot.getDimensions().getCorner1(), plot.getDimensions().getCorner2(), 0);

			blocks.clear();
			BlockPos pos = new BlockPos(plot.getPlotPos().getXi(), ConfigValues.y, plot.getPlotPos().getYi());
			AreaCacher cacher = new AreaCacher(Minecraft.getMinecraft().world, pos);
			blocks.putAll(cacher.blocks);
			vboCaches.clear();

			for (BlockRenderLayer layer : blocks.keySet()) {
				Tessellator tes = Tessellator.getInstance();
				BufferBuilder buffer0 = tes.getBuffer();
				BlockRendererDispatcher dispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();

				if (vboCaches.get(layer) == null) {

					buffer0.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

					for (IBlockState state2 : blocks.get(layer).keySet()) {
						for (BlockPos pos2 : blocks.get(layer).get(state2)) {
							dispatcher.renderBlock(state2, pos2, blockAccess, buffer0);
						}
					}

					vboCaches.put(layer, ClientUtilMethods.createCacheArrayAndReset(buffer0));
				}
			}

			// RENDER IT
			double guiSideWidth = 150 / 1.3;
			double tileSideSize = guiSideWidth / width;
			{

				ComponentVoid sideView = new ComponentVoid(0, 0, 100, 200);
				getMainComponents().add(sideView);

				//ScissorMixin.INSTANCE.scissor(sideView);

				sideView.BUS.hook(GuiComponentEvents.PostDrawEvent.class, (event) -> {
					int horizontalAngle = 35;
					int verticalAngle = 45;

					GlStateManager.pushMatrix();
					GlStateManager.enableCull();

					//GlStateManager.matrixMode(GL11.GL_MODELVIEW);
					//GlStateManager.shadeModel(GL11.GL_SMOOTH);
					GlStateManager.translate(150, 75 + (height / 2.0), 500);
					GlStateManager.rotate(horizontalAngle, -1, 0, 0);
					GlStateManager.rotate((float) (Minecraft.getMinecraft().world.getTotalWorldTime() * 10.0), 0, 1, 0);
					//GlStateManager.rotate((float) ((tick + event.getPartialTicks()) / 2), 0, 1, 0);
					GlStateManager.translate(tileSideSize, -tileSideSize, tileSideSize);
					GlStateManager.scale(tileSideSize, -tileSideSize, tileSideSize);
					GlStateManager.translate(-1.5, -1.5, -1.5);

					mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					for (BlockRenderLayer layer : blocks.keySet()) {
						Tessellator tes = Tessellator.getInstance();
						BufferBuilder buffer1 = tes.getBuffer();

						buffer1.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
						buffer1.addVertexData(vboCaches.get(layer));
						tes.draw();
					}

					GlStateManager.disableCull();
					GlStateManager.popMatrix();
				});
			}
			// SIDE VIEW //
		}
	}
}
