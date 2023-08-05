package wtf.atani.screen.click.ryu.component.impl;

import net.minecraft.client.gui.FontRenderer;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.value.Value;
import wtf.atani.value.impl.StringBoxValue;

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
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto Medium", 17);
        RenderUtil.drawRect(getPosX() + 1 + getAddX(), getPosY() + getAddY(), getBaseWidth() - 2, getFinalHeight(), new Color(22, 22, 25).getRGB());
        RenderUtil.startScissorBox();
        RenderUtil.drawScissorBox(getPosX() + 1 + getAddX(), getPosY() + getAddY(), getBaseWidth() - 2, getBaseHeight());
        normal.drawString(value.getName(), getPosX() + 10, getPosY() + getBaseHeight() / 2 - normal.FONT_HEIGHT / 2, -1);
        if(expanded) {
            float y = this.getPosY() + this.getBaseHeight();
            for(String string : stringBoxValue.getValues()) {
                RenderUtil.drawRect(getPosX() + 1, y, getBaseWidth() - 2, getBaseHeight(), new Color(18, 18, 18).getRGB());
                normal.drawStringWithShadow(string, getPosX() + 5 + getAddX() + 10, y + getBaseHeight() / 2 - normal.FONT_HEIGHT / 2,  stringBoxValue.getValue().equals(string) ? RYU : -1);
                y += this.getBaseHeight();
            }
        }
        RenderUtil.endScissorBox();
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
