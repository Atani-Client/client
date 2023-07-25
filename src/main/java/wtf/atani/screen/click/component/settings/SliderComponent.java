package wtf.atani.screen.click.component.settings;

import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.utils.math.MathUtil;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.value.Value;
import wtf.atani.value.impl.SliderValue;

import java.awt.*;

public class SliderComponent extends ValueComponent {

    private SliderValue value;
    private float posX, posY, width, elementHeight;

    public SliderComponent(SliderValue value, float posX, float posY, float width, float elementHeight) {
        this.value = value;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.elementHeight = elementHeight;
    }

    // This one's kinda shit, might recode later idk

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontStorage.getInstance().findFont("Roboto", 16).drawStringWithShadow(value.getName() + " - " + value.getValue().floatValue(), posX + 5, posY + 3, -1);
        System.out.println(3 + FontStorage.getInstance().findFont("Roboto", 16).FONT_HEIGHT);
        double min = value.getMinimum().doubleValue();
        double max = value.getMaximum().doubleValue();
        float sliderY = posY + 13;
        float sliderHeight = 2;
        float sliderX = posX + 3 + FontStorage.getInstance().findFont("Roboto", 14).getStringWidth(min + "") + 2;
        float sliderWidth = width - 6 - FontStorage.getInstance().findFont("Roboto", 14).getStringWidth(min + "") - 2 - FontStorage.getInstance().findFont("Roboto", 14).getStringWidth(max + "");
        FontStorage.getInstance().findFont("Roboto", 14).drawString(min + "", posX + 3, sliderY - 1, - 1);
        FontStorage.getInstance().findFont("Roboto", 14).drawString(max + "", posX + width - 1 - FontStorage.getInstance().findFont("Roboto", 14).getStringWidth(max + ""), sliderY - 1, - 1);
        RenderUtil.drawRect(sliderX, sliderY, sliderWidth, sliderHeight, new Color(0, 0, 0, 50).getRGB());
        float length = MathHelper
                .floor_double(((value.getValue()).floatValue() - value.getMinimum().floatValue())
                        / (value.getMaximum().floatValue() - value.getMinimum().floatValue()) * sliderWidth);
        RenderUtil.drawRect(sliderX, sliderY, length, sliderHeight, new Color(0, 0, 0, 90).getRGB());
        if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, posX, posY, width, elementHeight)) {
            double newValue = (mouseX - sliderX) * (max - min) / (sliderWidth - 1.0f) + min;
            value.setValue(newValue);
        }
     }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public float getFinalHeight() {
        return elementHeight;
    }

    @Override
    public Value getValue() {
        return value;
    }
}
