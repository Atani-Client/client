package tech.atani.client.feature.guis.screens.clickgui.simple.component.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.utility.math.MathUtil;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.feature.value.Value;
import tech.atani.client.feature.value.impl.SliderValue;

import java.awt.*;

public class SliderComponent extends ValueComponent {

    private boolean expanded = false;

    public SliderComponent(Value value, float posX, float posY, float baseWidth, float baseHeight) {
        super(value, posX, posY, baseWidth, baseHeight);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto", 17);
        RenderUtil.drawRect(getPosX() + getAddX(), getPosY(), getBaseWidth(), getBaseHeight(), new Color(0, 0, 0, 180).getRGB());
        normal.drawStringWithShadow(value.getName(), getPosX() + 5 + getAddX(), getPosY() + getBaseHeight() / 2 - normal.FONT_HEIGHT / 2, -1);
        normal.drawStringWithShadow(((Number)value.getValue()).floatValue() + "", getPosX() + this.getBaseWidth() - 5 - normal.getStringWidth(((Number)value.getValue()).floatValue() + "") + getAddX(), getPosY() + getBaseHeight() / 2 - normal.FONT_HEIGHT / 2, -1);
        if(this.expanded) {
            RenderUtil.drawRect(getPosX() + getAddX(), getPosY() + getBaseHeight(), getBaseWidth(), getBaseHeight(), new Color(0, 0, 0, 180).getRGB());
            SliderValue sliderValue = (SliderValue) value;
            String min = sliderValue.getMinimum().floatValue() + "";
            String max = sliderValue.getMaximum().floatValue() + "";
            normal.drawStringWithShadow(min, getPosX() + 5 + getAddX(), getPosY() + getBaseHeight() + getBaseHeight() / 2 - normal.FONT_HEIGHT / 2, -1);
            normal.drawStringWithShadow(max, getPosX() + this.getBaseWidth() - 5 - normal.getStringWidth(max) + getAddX(), getPosY() + getBaseHeight() + getBaseHeight() / 2 - normal.FONT_HEIGHT / 2, -1);
            float sliderX = getPosX() + 5 + normal.getStringWidth(min) + 3 + getAddX();
            float sliderWidth = getBaseWidth() - (5 + normal.getStringWidth(min) + 3) - (5 + normal.getStringWidth(max) + 3);
            float sliderY = getPosY() + getBaseHeight() + getBaseHeight() / 4 - 1;
            float sliderHeight = getBaseHeight() / 4 * 2;
            RenderUtil.drawRect(sliderX, sliderY, sliderWidth, sliderHeight, new Color(0, 0, 0, 50).getRGB());
            float length = MathHelper
                    .floor_double(((sliderValue.getValue()).floatValue() - sliderValue.getMinimum().floatValue())
                            / (sliderValue.getMaximum().floatValue() - sliderValue.getMinimum().floatValue()) * sliderWidth);
            RenderUtil.drawRect(sliderX, sliderY, length, sliderHeight, new Color(0, 0, 0, 90).getRGB());
            if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, this.getPosX() + getAddX(), this.getPosY() + this.getBaseHeight(), this.getBaseWidth(), this.getBaseHeight())) {
                double min1 = sliderValue.getMinimum().floatValue();
                double max1 = sliderValue.getMaximum().floatValue();
                double newValue = MathUtil.round((mouseX - sliderX) * (max1 - min1) / (sliderWidth - 1.0f) + min1, sliderValue.getDecimalPlaces());
                sliderValue.setValue(newValue);
            }
        }
    }

    @Override
    public float getFinalHeight() {
        return this.expanded ? this.getBaseHeight() * 2 : this.getBaseHeight();
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovered(mouseX, mouseY, this.getPosX(), this.getPosY(), this.getBaseWidth(), this.getBaseHeight())) {
            expanded = !expanded;
        }
    }
}
