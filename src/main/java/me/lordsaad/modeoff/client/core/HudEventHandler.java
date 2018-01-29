package me.lordsaad.modeoff.client.core;

import me.lordsaad.modeoff.api.plot.Plot;
import me.lordsaad.modeoff.api.plot.PlotRegistry;
import me.lordsaad.modeoff.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Saad on 6/20/2016.
 */
public class HudEventHandler extends Gui {

	@SubscribeEvent
	public void renderHud(Post event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.EXPERIENCE) return;

		ScaledResolution resolution = event.getResolution();
		int width = resolution.getScaledWidth();
		int height = resolution.getScaledHeight();
		EntityPlayer player = Minecraft.getMinecraft().player;

		Plot plot = PlotRegistry.INSTANCE.findPlot(player.getPosition());
		if (plot != null) {
			int right = ((width / 2) - (100 / 2)) + 145;
			int top = height - 17;
			int paneWidth = 120, paneHeight;
			int r = 0, g = 0, b = 0, a = 100;

			Set<UUID> authors = plot.getOwners();
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

			paneHeight = 6 + fontRenderer.FONT_HEIGHT * 2 + fontRenderer.FONT_HEIGHT * authors.size();

			GlStateManager.pushMatrix();
			GlStateManager.disableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.enableAlpha();

			GlStateManager.translate(width - paneWidth, (height / 2.0) - paneHeight, 0);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(0, 0, 0).color(r, g, b, a).endVertex();
			buffer.pos(0, paneHeight, 0).color(r, g, b, a).endVertex();
			buffer.pos(paneWidth, paneHeight, 0).color(r, g, b, a).endVertex();
			buffer.pos(paneWidth, 0, 0).color(r, g, b, a).endVertex();

			tessellator.draw();

			GlStateManager.enableTexture2D();

			fontRenderer.drawString("Mod: " + plot.getModName(), 3, 3, 0xFFFFFF);

			if (authors.size() == 1) {
				ArrayList<UUID> authorList = new ArrayList<>(authors);
				fontRenderer.drawString("Author: " + CommonProxy.playerUUIDMap.inverse().get(authorList.get(0)), 3, 3 + fontRenderer.FONT_HEIGHT, 0xFFFFFF);
			} else {
				fontRenderer.drawString("Authors:", 3, 3 + fontRenderer.FONT_HEIGHT, 0xFFFFFF);
				int count = 2;
				for (UUID uuid : authors) {
					String name = CommonProxy.playerUUIDMap.inverse().get(uuid);
					fontRenderer.drawString("  | " + name, 3, 3 + (fontRenderer.FONT_HEIGHT * count), 0xFFFFFF);
					count++;
				}
			}


			GlStateManager.disableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.popMatrix();
		}
	}
}
