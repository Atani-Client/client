package wtf.atani.screen.click.icarus.window.component.impl;

import net.minecraft.client.gui.FontRenderer;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.utils.render.GradientUtil;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.RoundedUtil;
import wtf.atani.value.impl.CheckBoxValue;

import java.awt.*;

public class CheckboxComponent extends ValueComponent {

    private final CheckBoxValue checkBoxValue;
    private FontRenderer fontRenderer = FontStorage.getInstance().findFont("ArialMT", 16);

    public CheckboxComponent(CheckBoxValue checkBoxValue, float posX, float posY, float height) {
        super(checkBoxValue, posX, posY, height);
        this.checkBoxValue = checkBoxValue;
    }

    @Override
    public float draw(int mouseX, int mouseY) {
        RoundedUtil.drawGradientRound(getPosX() + 6, getPosY() + 4, 11, getHeight() - 8, 2, checkBoxValue.isEnabled() ? new Color(ICARUS_FIRST) : new Color(30, 30, 30), checkBoxValue.isEnabled() ? new Color(ICARUS_FIRST) : new Color(30, 30, 30), checkBoxValue.isEnabled() ? new Color(ICARUS_SECOND) : new Color(30, 30, 30), checkBoxValue.isEnabled() ? new Color(ICARUS_SECOND) : new Color(30, 30, 30));
        fontRenderer.drawString(checkBoxValue.getName(), getPosX() + 25, getPosY() + getFinalHeight() / 2 - fontRenderer.FONT_HEIGHT / 2 + 1, -1);
        return fontRenderer.getStringWidth(checkBoxValue.getName()) + 25 + 2;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovered(mouseX, mouseY, getPosX(), getPosY(), getWidth(), getHeight()) && mouseButton == 0) {
            checkBoxValue.setValue(!checkBoxValue.getValue());
        }
    }

}
