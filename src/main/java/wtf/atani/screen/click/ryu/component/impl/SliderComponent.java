package wtf.atani.screen.click.ryu.component.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.shader.legacy.shaders.RoundedShader;
import wtf.atani.value.Value;
import wtf.atani.value.impl.SliderValue;

import java.awt.*;

public class SliderComponent extends ValueComponent {

    private boolean expanded = false;

    public SliderComponent(Value value, float posX, float posY, float baseWidth, float baseHeight) {
        super(value, posX, posY, baseWidth, baseHeight);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto Medium", 16);
        RenderUtil.drawRect(getPosX() + 1 + getAddX(), getPosY() + getAddY(), getBaseWidth() - 2, getBaseHeight(), new Color(22, 22, 25).getRGB());
        RenderUtil.startScissorBox();
        RenderUtil.drawScissorBox(getPosX() + 1 + getAddX(), getPosY() + getAddY(), getBaseWidth() - 2, getBaseHeight());
        normal.drawString(value.getName(), getPosX() + 5 + getAddX(), getPosY() + 2f, -1);
        SliderValue sliderValue = (SliderValue) value;
        float sliderX = getPosX() + 7, sliderY = getPosY() + 10, sliderWidth = getBaseWidth() - 14, sliderHeight = 1.5f;
        float length = MathHelper
                .floor_double(((sliderValue.getValue()).floatValue() - sliderValue.getMinimum().floatValue())
                        / (sliderValue.getMaximum().floatValue() - sliderValue.getMinimum().floatValue()) * sliderWidth);
        RoundedShader.drawRound(sliderX, sliderY, length, sliderHeight, 2, new Color(RYU));
        RoundedShader.drawRound(sliderX + length - 2, sliderY - 2 + 1, 4, 4, 2, new Color(-1));
        FontRenderer small = FontStorage.getInstance().findFont("Roboto Medium", 15);
        small.drawString(sliderValue.getValue().floatValue() + "", sliderX + length - small.getStringWidth(sliderValue.getValue().floatValue() + "") / 2, sliderY + 4, -1);
        RenderUtil.endScissorBox();
    }

    @Override
    public float getFinalHeight() {
        return  this.getBaseHeight();
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovered(mouseX, mouseY, this.getPosX(), this.getPosY(), this.getBaseWidth(), this.getBaseHeight())) {
            expanded = !expanded;
        }
    }
}
