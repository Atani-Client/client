package wtf.atani.screen.click.ryu.component.impl;

import net.minecraft.client.gui.FontRenderer;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.RoundedUtil;
import wtf.atani.value.Value;
import wtf.atani.value.impl.CheckBoxValue;

import java.awt.*;

public class CheckBoxComponent extends ValueComponent {

    public CheckBoxComponent(Value value, float posX, float posY, float baseWidth, float baseHeight) {
        super(value, posX, posY, baseWidth, baseHeight);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto Medium", 17);
        RenderUtil.drawRect(getPosX() + 1 + getAddX(), getPosY() + getAddY(), getBaseWidth() - 2, getBaseHeight(), new Color(22, 22, 25).getRGB());
        RenderUtil.startScissorBox();
        RenderUtil.drawScissorBox(getPosX() + 1 + getAddX(), getPosY() + getAddY(), getBaseWidth() - 2, getBaseHeight());
        normal.drawString(value.getName(), getPosX() + 25, getPosY() + getBaseHeight() / 2 - normal.FONT_HEIGHT / 2, -1);
        RoundedUtil.drawRound(getPosX() + 8, getPosY() + 5, 10, getBaseHeight() - 5 * 2, 2, new Color(36, 37, 41));
        CheckBoxValue checkBoxValue = (CheckBoxValue) value;
        if(checkBoxValue.isEnabled()) {
            normal.drawString("✔", getPosX() + 8 + 1, getPosY() + 5 + 1, RYU);
        }
        RenderUtil.endScissorBox();
     }

    @Override
    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovered(mouseX, mouseY, this.getPosX(), this.getPosY(), this.getBaseWidth(), this.getBaseHeight())) {
            CheckBoxValue checkBoxValue = (CheckBoxValue) value;
            checkBoxValue.setValue(!checkBoxValue.getValue());
        }
    }
}
