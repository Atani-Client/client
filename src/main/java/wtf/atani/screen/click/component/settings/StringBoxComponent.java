package wtf.atani.screen.click.component.settings;

import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.click.component.Component;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.value.Value;
import wtf.atani.value.impl.StringBoxValue;

public class StringBoxComponent extends ValueComponent {

    private StringBoxValue value;
    private float posX, posY, width, elementHeight;
    private boolean expanded = false;

    public StringBoxComponent(StringBoxValue value, float posX, float posY, float width, float elementHeight) {
        this.value = value;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.elementHeight = elementHeight;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontStorage.getInstance().findFont("Roboto", 19).drawStringWithShadow(value.getName() + " - " + value.getValue(), posX + 5, posY + elementHeight / 2 - FontStorage.getInstance().findFont("Roboto", 19).FONT_HEIGHT / 2, -1);
        FontStorage.getInstance().findFont("Roboto", 19).drawCenteredStringWithShadow(expanded ? "<" : ">", posX + width - 12, posY + elementHeight / 2 - FontStorage.getInstance().findFont("Roboto", 19).FONT_HEIGHT / 2, -1);
        if(expanded) {
            float y = this.posY + this.elementHeight;
            for(String string : value.getValues()) {
                FontStorage.getInstance().findFont("Roboto", 19).drawStringWithShadow(" - " + string, posX + 5, y + elementHeight / 2 - FontStorage.getInstance().findFont("Roboto", 19).FONT_HEIGHT / 2, -1);
                y += this.elementHeight;
            }
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovered(mouseX, mouseY, this.posX, this.posY, this.width, this.elementHeight)) {
            expanded = !expanded;
        }
        if(expanded) {
            float y = this.posY + this.elementHeight;
            for(String string : value.getValues()) {
                if(RenderUtil.isHovered(mouseX, mouseY, this.posX, y, this.width, this.elementHeight)) {
                    value.setValue(string);
                }
                y += this.elementHeight;
            }
        }
    }

    @Override
    public float getFinalHeight() {
        return elementHeight + (this.expanded ? value.getValues().length * this.elementHeight : 0);
    }

    @Override
    public Value getValue() {
        return value;
    }
}
