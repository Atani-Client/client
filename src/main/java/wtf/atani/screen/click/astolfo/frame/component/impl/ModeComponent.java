package wtf.atani.screen.click.astolfo.frame.component.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import wtf.atani.value.impl.StringBoxValue;

import java.awt.*;

public class ModeComponent extends ValueComponent {

    private final StringBoxValue stringBoxValue;
    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

    public ModeComponent(StringBoxValue stringBoxValue, float x, float y, float width, float height) {
        super(stringBoxValue, x, y, width, height);
        this.stringBoxValue = stringBoxValue;
    }

    @Override
    public void drawScreen(int mx, int my) {
        Gui.drawRect(x, y, x + width, y + height, new Color(25,25,25).getRGB());
        fontRenderer.drawHeightCenteredString(stringBoxValue.getName(), x + 4, y + height / 2, -1);
        fontRenderer.drawHeightCenteredString(stringBoxValue.getValue(), x + 4 + 93 - fontRenderer.getStringWidth(stringBoxValue.getValue()), y + height / 2, -1);
    }

    @Override
    public void actionPerformed(int x, int y, boolean click, int button) {
        if (isHovered(x, y) && click) {
            String[] values = stringBoxValue.getValues();
            String currentValue = stringBoxValue.getValue();
            int currentIndex = findIndexOfValue(values, currentValue);

            if (currentIndex >= 0) {
                if (button == 0) {
                    int nextIndex = (currentIndex + 1) % values.length;
                    stringBoxValue.setValue(values[nextIndex]);
                } else if (button == 1) {
                    int prevIndex = (currentIndex - 1 + values.length) % values.length;
                    stringBoxValue.setValue(values[prevIndex]);
                }
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) { }

    private int findIndexOfValue(String[] values, String value) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
