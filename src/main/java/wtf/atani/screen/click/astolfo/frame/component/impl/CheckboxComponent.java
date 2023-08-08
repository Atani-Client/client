package wtf.atani.screen.click.astolfo.frame.component.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import wtf.atani.utils.render.color.ColorUtil;
import wtf.atani.value.impl.CheckBoxValue;

import java.awt.*;

public class CheckboxComponent extends ValueComponent {

    private final CheckBoxValue checkBoxValue;
    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

    public CheckboxComponent(CheckBoxValue checkBoxValue, float x, float y, float width, float height) {
        super(checkBoxValue, x, y, width, height);
        this.checkBoxValue = checkBoxValue;
    }

    @Override
    public void drawScreen(int mx, int my) {
        int counter = 0;
        Gui.drawRect(x, y, x + width, y + height, new Color(25,25,25).getRGB());
        if(checkBoxValue.isEnabled()) {
            Gui.drawRect(x + 3, y, x + width - 3, y + height, new Color(ColorUtil.blendRainbowColours(counter * 150L)).getRGB());
        }
        fontRenderer.drawHeightCenteredString(checkBoxValue.getName(), x + 4, y + height / 2, -1);
        counter++;
    }

    @Override
    public void actionPerformed(int x, int y, boolean click, int button) {
        if(isHovered(x, y) && click) {
            checkBoxValue.toggle();
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {}
}
