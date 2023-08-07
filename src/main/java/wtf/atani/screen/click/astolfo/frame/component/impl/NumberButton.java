package wtf.atani.screen.click.astolfo.frame.component.impl;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import wtf.atani.screen.click.astolfo.frame.component.Component;
import wtf.atani.value.impl.SliderValue;

import java.awt.*;

import static wtf.atani.utils.interfaces.Methods.mc;

public class NumberButton extends Component {
    public SliderValue setting;
    public Color color;

    public boolean dragged;

    public NumberButton(float x, float y, float width, float height, SliderValue setting, Color color) {
        super(x, y, width, height);
        this.setting = setting;
        this.color = color;
    }

    @Override
    public void drawScreen(int x, int y) {
        double diff = setting.getMaximum().doubleValue() - setting.getMinimum().doubleValue();

        double widthPrec = (setting.getValue().doubleValue() - setting.getMaximum().doubleValue()) / (setting.getMaximum().doubleValue() - setting.getMinimum().doubleValue());

        int handleWidth = 3;

        if(dragged) {
            double value = setting.getMinimum().doubleValue() + MathHelper.clamp_double((double)(x - this.x - handleWidth / 2) / (width - handleWidth), 0, 1) * diff;
            setting.setValue(Math.round(value * 100D) / 100D);
        }

        Gui.drawRect(this.x, this.y, this.x + width, this.y + height, 0xff181A17);
        Gui.drawRect(this.x + 1, this.y, this.x + (int)(widthPrec * (width - 2)) + 1, this.y + height, color.getRGB());


        mc.fontRendererObj.drawCenteredStringWithShadow(setting.getName() + ": " + Math.round(setting.getValue().doubleValue() * 100D) / 100D, this.x + 4, this.y + height / 2, -1);
    }


    @Override
    public void actionPerformed(int x, int y, boolean click, int button) {
        if(isHovered(x, y)){
            dragged = true;
        }

        if(!click)
            dragged = false;
    }

    @Override
    public void key(char typedChar, int key) {}
}
