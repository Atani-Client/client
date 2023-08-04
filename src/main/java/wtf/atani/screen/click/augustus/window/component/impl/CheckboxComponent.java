package wtf.atani.screen.click.augustus.window.component.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.click.augustus.window.component.Component;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.value.impl.CheckBoxValue;

import java.awt.*;

public class CheckboxComponent extends ValueComponent {

    private final CheckBoxValue checkBoxValue;
    private FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

    public CheckboxComponent(CheckBoxValue checkBoxValue, float posX, float posY, float height) {
        super(checkBoxValue, posX, posY, height);
        this.checkBoxValue = checkBoxValue;
    }

    @Override
    public float draw(int mouseX, int mouseY) {
        fontRenderer.drawStringWithShadow("x", getPosX() + 5, getPosY() + getFinalHeight() / 2 - fontRenderer.FONT_HEIGHT / 2 - 1, !checkBoxValue.getValue() ? new Color(139, 141, 145, 255).getRGB() : new Color(41, 146, 222).getRGB());
        fontRenderer.drawStringWithShadow(checkBoxValue.getName(), getPosX() + 13 + fontRenderer.getStringWidth("X"), getPosY() + getFinalHeight() / 2 - fontRenderer.FONT_HEIGHT / 2, new Color(139, 141, 145, 255).getRGB());
        return fontRenderer.getStringWidth(checkBoxValue.getName()) + 15 + fontRenderer.getStringWidth("X");
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovered(mouseX, mouseY, getPosX(), getPosY(), getWidth(), getHeight()) && mouseButton == 0) {
            checkBoxValue.setValue(!checkBoxValue.getValue());
        }
    }

}
