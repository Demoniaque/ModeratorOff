package me.lordsaad.modeoff.client.gui;

import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.features.gui.components.ComponentVoid;
import com.teamwizardry.librarianlib.features.gui.mixin.ScissorMixin;
import com.teamwizardry.librarianlib.features.kotlin.ClientUtilMethods;
import com.teamwizardry.librarianlib.features.network.PacketHandler;
import com.teamwizardry.librarianlib.features.sprite.Sprite;
import me.lordsaad.modeoff.ModeratorOff;
import me.lordsaad.modeoff.api.ConfigValues;
import me.lordsaad.modeoff.api.PlotCacher;
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
import net.minecraft.init.SoundEvents;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
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
		new ComponentButton(20, 20 + (id * 20) + (id * buffer), compRect, "Lock Plot", plot.hasTag("lock_plot") ? CHECKBOX_CHECKED : CHECKBOX_XED, (componentSprite, componentText) -> {
			String tag = "lock_plot";
			if (plot.hasTag(tag)) {
				plot.removeTag(tag);
			} else plot.addTag(tag);

			if (plot.hasTag(tag)) {
				componentSprite.setSprite(CHECKBOX_CHECKED);
			} else componentSprite.setSprite(CHECKBOX_XED);

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
			Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, plot.hasTag(tag) ? 2f : 1f);
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If a plot is locked, no one including the plot owners (yourself) will be able to edit the plot.");
			return txt;
		});

		new ComponentButton(20, 20 + (++id * 20) + (id * buffer), compRect, "Disable Block Placing", plot.hasTag("edit_plot_place") ? CHECKBOX_CHECKED : CHECKBOX_XED, (componentSprite, componentText) -> {
			String tag = "edit_plot_place";
			if (plot.hasTag(tag)) {
				plot.removeTag(tag);
			} else plot.addTag(tag);

			if (plot.hasTag(tag)) {
				componentSprite.setSprite(CHECKBOX_CHECKED);
			} else componentSprite.setSprite(CHECKBOX_XED);

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
			Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, plot.hasTag(tag) ? 2f : 1f);
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If enabled, viewers of the plot (your audience) will be able to place cacher.blocks on the plot");
			return txt;
		});

		new ComponentButton(20, 20 + (++id * 20) + (id * buffer), compRect, "Disable Block Breaking", plot.hasTag("edit_plot_break") ? CHECKBOX_CHECKED : CHECKBOX_XED, (componentSprite, componentText) -> {
			String tag = "edit_plot_break";
			if (plot.hasTag(tag)) {
				plot.removeTag(tag);
			} else plot.addTag(tag);

			if (plot.hasTag(tag)) {
				componentSprite.setSprite(CHECKBOX_CHECKED);
			} else componentSprite.setSprite(CHECKBOX_XED);

			PacketHandler.NETWORK.sendToServer(new PacketUpdatePlot(plot));
			Minecraft.getMinecraft().player.playSound(SoundEvents.BLOCK_NOTE_BELL, 1, plot.hasTag(tag) ? 2f : 1f);
		}).render.getTooltip().func((Function<GuiComponent, java.util.List<String>>) t -> {
			List<String> txt = new ArrayList<>();
			txt.add("If enabled, viewers of the plot (your audience) will be able to break cacher.blocks on the plot");
			return txt;
		});


		//if (false) {
		Plot.PlotDimensions dimensions = plot.getDimensions();
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
		{

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
		}
		// SIDE VIEW //
		//}
	}
}
