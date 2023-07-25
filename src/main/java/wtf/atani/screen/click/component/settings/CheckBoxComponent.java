package wtf.atani.screen.click.component.settings;

import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.click.component.Component;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.value.Value;
import wtf.atani.value.impl.CheckBoxValue;

public class CheckBoxComponent extends ValueComponent {

    private CheckBoxValue value;
    private float posX, posY, width, elementHeight;

    public CheckBoxComponent(CheckBoxValue value, float posX, float posY, float width, float elementHeight) {
        this.value = value;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.elementHeight = elementHeight;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontStorage.getInstance().findFont("Roboto", 19).drawStringWithShadow(value.getName(), posX + 5, posY + elementHeight / 2 - FontStorage.getInstance().findFont("Roboto", 19).FONT_HEIGHT / 2, -1);
        if(this.value.getValue())
            RenderUtil.drawCheckMark(this.posX + this.width - 13, this.posY + 5, 10, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovered(mouseX, mouseY, this.posX, this.posY, this.width, this.elementHeight)) {
            value.setValue(!value.getValue());
        }
    }

    @Override
    public float getFinalHeight() {
        return elementHeight;
    }

    @Override
    public Value getValue() {
        return value;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }
}
