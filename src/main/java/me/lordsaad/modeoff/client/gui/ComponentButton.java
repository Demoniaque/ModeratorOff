package me.lordsaad.modeoff.client.gui;

import com.teamwizardry.librarianlib.features.gui.component.GuiComponent;
import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents;
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.features.gui.components.ComponentSprite;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;
import com.teamwizardry.librarianlib.features.sprite.Sprite;

import java.awt.*;
import java.util.function.BiConsumer;

public class ComponentButton extends GuiComponent {

	public ComponentButton(int posX, int posY, GuiComponent parent, String text, Sprite initialSprite, BiConsumer<ComponentSprite, ComponentText> onClick) {
		super(posX, posY, 170, 16);

		parent.add(this);

		getTransform().setScale(2);
		ComponentRect compRect = new ComponentRect(0, 0, getSize().getXi(), getSize().getYi());
		compRect.getColor().setValue(new Color(0x80FFFFFF, true));
		add(compRect);

		ComponentText compText = new ComponentText(20, (int) (getSize().getY() / 2.0), ComponentText.TextAlignH.LEFT, ComponentText.TextAlignV.MIDDLE);
		compText.getText().setValue(text);
		compRect.add(compText);

		ComponentSprite compSprite = new ComponentSprite(initialSprite, 0, 0, 16, 16);
		compRect.add(compSprite);

		compRect.BUS.hook(GuiComponentEvents.MouseClickEvent.class, mouseClickEvent -> {
			onClick.accept(compSprite, compText);
		});

		compRect.BUS.hook(GuiComponentEvents.MouseInEvent.class, mouseInEvent -> {
			compRect.getColor().setValue(new Color(0xB3FFFFFF, true));
		});

		compRect.BUS.hook(GuiComponentEvents.MouseOutEvent.class, mouseOutEvent -> {
			compRect.getColor().setValue(new Color(0x80FFFFFF, true));
		});
	}
}
