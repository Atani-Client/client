package tech.atani.client.feature.guis.screens.clickgui.simple.component.impl;

import net.minecraft.client.gui.FontRenderer;
import tech.atani.client.feature.customFont.storage.FontStorage;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.feature.module.value.Value;
import tech.atani.client.feature.module.value.impl.StringBoxValue;

import java.awt.*;

public class StringBoxComponent extends ValueComponent {

    private boolean expanded = false;
    private StringBoxValue stringBoxValue;

    public StringBoxComponent(Value value, float posX, float posY, float baseWidth, float baseHeight) {
        super(value, posX, posY, baseWidth, baseHeight);
        this.stringBoxValue = (StringBoxValue) value;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto", 17);
        RenderUtil.drawRect(getPosX() + getAddX(), getPosY(), getBaseWidth(), getBaseHeight(), new Color(0, 0, 0, 180).getRGB());
        normal.drawStringWithShadow(value.getName(), getPosX() + 5 + getAddX(), getPosY() + getBaseHeight() / 2 - normal.FONT_HEIGHT / 2, -1);
        normal.drawStringWithShadow(value.getValue().toString(), getPosX() + this.getBaseWidth() - 5 - normal.getStringWidth(value.getValue().toString()) + getAddX(), getPosY() + getBaseHeight() / 2 - normal.FONT_HEIGHT / 2,-1);
        if(expanded) {
            float y = this.getPosY() + this.getBaseHeight();
            for(String string : stringBoxValue.getValues()) {
                RenderUtil.drawRect(getPosX() + getAddX(), y, getBaseWidth(), getBaseHeight(), new Color(0, 0, 0, 180).getRGB());
                normal.drawStringWithShadow(" - " + string, getPosX() + 5 + getAddX(), y + getBaseHeight() / 2 - normal.FONT_HEIGHT / 2, -1);
                y += this.getBaseHeight();
            }
        }
    }

    @Override
    public float getFinalHeight() {
        return this.expanded ? this.getBaseHeight() + this.getBaseHeight() * stringBoxValue.getValues().length : this.getBaseHeight();
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovered(mouseX, mouseY, this.getPosX() + getAddX(), this.getPosY(), this.getBaseWidth(), this.getBaseHeight())) {
            expanded = !expanded;
        }
        if(expanded) {
            float y = this.getPosY() + this.getBaseHeight();
            for(String string : stringBoxValue.getValues()) {
                if(RenderUtil.isHovered(mouseX, mouseY, this.getPosX() + getAddX(), y, this.getBaseWidth(), this.getBaseHeight())) {
                    value.setValue(string);
                }
                y += this.getBaseHeight();
            }
        }
    }
}
