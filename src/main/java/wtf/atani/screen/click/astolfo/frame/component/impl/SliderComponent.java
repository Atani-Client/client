package wtf.atani.screen.click.astolfo.frame.component.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import wtf.atani.utils.render.color.ColorUtil;
import wtf.atani.value.impl.SliderValue;

import java.awt.*;

public class SliderComponent extends ValueComponent {

    private final SliderValue sliderValue;
    private final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

    public boolean dragged;

    public SliderComponent(SliderValue sliderValue,float x, float y, float width, float height) {
        super(sliderValue, x, y, width, height);
        this.sliderValue = sliderValue;
    }

    @Override
    public void drawScreen(int x, int y) {
        double diff = sliderValue.getMaximum().doubleValue() - sliderValue.getMinimum().doubleValue();

        double widthPrec = (sliderValue.getValue().doubleValue() - sliderValue.getMinimum().doubleValue()) / (sliderValue.getMaximum().doubleValue() - sliderValue.getMinimum().doubleValue());

        int handleWidth = 3;
        int counter = 0;

        if (dragged) {
            double value = sliderValue.getMinimum().doubleValue() + MathHelper.clamp_double((double) (x - this.x - handleWidth / 2) / (width - handleWidth), 0, 1) * diff;
            sliderValue.setValue(Math.round(value * 100D) / 100D);
        }

        float sliderWidth = width - 4;
        float length = MathHelper.floor_double(widthPrec * sliderWidth);

        Gui.drawRect(this.x, this.y, this.x + (int) width, this.y + (int) height, new Color(25, 25, 25).getRGB());
        Gui.drawRect(this.x + 1, this.y, this.x + (int) length + 1, this.y + (int) height, new Color(ColorUtil.blendRainbowColours(counter * 150L)).getRGB());

        fontRenderer.drawHeightCenteredString(sliderValue.getName() + ": " + Math.round(sliderValue.getValue().doubleValue() * 100D) / 100D, this.x + 4, this.y + height / 2, -1);
        counter++;
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
    public void keyTyped(char typedChar, int key) {}
}
