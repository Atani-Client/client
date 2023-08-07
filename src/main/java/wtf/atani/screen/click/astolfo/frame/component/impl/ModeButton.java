package wtf.atani.screen.click.astolfo.frame.component.impl;

import net.minecraft.client.gui.Gui;
import wtf.atani.screen.click.astolfo.frame.component.Component;
import wtf.atani.value.impl.StringBoxValue;

import java.awt.*;

import static wtf.atani.utils.interfaces.Methods.mc;

public class ModeButton extends Component {
    public StringBoxValue setting;
    public Color color;

    public ModeButton(float x, float y, float width, float height, StringBoxValue setting, Color color) {
        super(x, y, width, height);
        this.setting = setting;
        this.color = color;
    }

    @Override
    public void drawScreen(int mx, int my) {
        Gui.drawRect(x, y, x + width, y + height, 0xff181A17);
        mc.fontRendererObj.drawCenteredStringWithShadow(setting.getName(), x + 4, y + height / 2, -1);
        mc.fontRendererObj.drawCenteredStringWithShadow(setting.getValue(), x + 4 + 93 - mc.fontRendererObj.getStringWidth(setting.getValue()), y +height / 2, -1);
    }

    @Override
    public void actionPerformed(int x, int y, boolean click, int button) {
        if(isHovered(x, y) && click) {
            if(button == 0)
                setting.setValue("Bruh");
            else if(button == 1)
                setting.setValue("Next");
        }
    }

    @Override
    public void key(char typedChar, int key) { }
}
