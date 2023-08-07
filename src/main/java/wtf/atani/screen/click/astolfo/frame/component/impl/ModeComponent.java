package wtf.atani.screen.click.astolfo.frame.component.impl;

import net.minecraft.client.gui.Gui;
import wtf.atani.screen.click.astolfo.frame.component.Component;
import wtf.atani.value.impl.StringBoxValue;

import java.awt.*;

public class ModeComponent extends Component {
    public StringBoxValue setting;
    public Color color;

    public ModeComponent(float x, float y, float width, float height, StringBoxValue setting, Color color) {
        super(x, y, width, height);
        this.setting = setting;
        this.color = color;
    }

    @Override
    public void drawScreen(int mx, int my) {
        Gui.drawRect(x, y, x + width, y + height, new Color(25,25,25).getRGB());
        fontRenderer.drawHeightCenteredString(setting.getName(), x + 4, y + height / 2, -1);
        fontRenderer.drawHeightCenteredString(setting.getValue(), x + 4 + 93 - fontRenderer.getStringWidth(setting.getValue()), y + height / 2, -1);
    }

    @Override
    public void actionPerformed(int x, int y, boolean click, int button) {
        if (isHovered(x, y) && click) {
            String[] values = setting.getValues();
            String currentValue = setting.getValue();
            int currentIndex = findIndexOfValue(values, currentValue);

            if (currentIndex >= 0) {
                if (button == 0) {
                    int nextIndex = (currentIndex + 1) % values.length;
                    setting.setValue(values[nextIndex]);
                } else if (button == 1) {
                    int prevIndex = (currentIndex - 1 + values.length) % values.length;
                    setting.setValue(values[prevIndex]);
                }
            }
        }
    }

    @Override
    public void key(char typedChar, int key) { }

    private int findIndexOfValue(String[] values, String value) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }
}
