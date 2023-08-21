package tech.atani.client.feature.guis.screens.clickgui.augustus.window;

import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.guis.screens.clickgui.augustus.window.component.impl.CheckboxComponent;
import tech.atani.client.feature.guis.screens.clickgui.augustus.window.component.impl.ModeComponent;
import tech.atani.client.feature.guis.screens.clickgui.augustus.window.component.impl.SliderComponent;
import tech.atani.client.feature.guis.screens.clickgui.augustus.window.component.impl.ValueComponent;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.feature.value.Value;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.feature.value.storage.ValueStorage;

import java.awt.*;
import java.util.ArrayList;

public class Window {

    private final Module module;
    private float posX, posY;
    private float width, height;
    private final float defaultWidth = 100;
    private final ArrayList<ValueComponent> components = new ArrayList<>();

    public Window(Module module, float posX, float posY) {
        this.module = module;
        this.posX = posX;
        this.posY = posY;

        this.width = defaultWidth;

        float yPos = getPosY();

        for(Value value : ValueStorage.getInstance().getValues(module)) {
            if(value instanceof CheckBoxValue) {
                CheckboxComponent component = new CheckboxComponent((CheckBoxValue) value, posX, yPos, 14);
                this.components.add(component);
                yPos += component.getFinalHeight();
            } else if(value instanceof SliderValue) {
                SliderComponent component = new SliderComponent((SliderValue) value, posX, yPos, 14);
                this.components.add(component);
                yPos += component.getFinalHeight();
            }else if(value instanceof StringBoxValue) {
                ModeComponent component = new ModeComponent((StringBoxValue) value, posX, yPos, 14);
                this.components.add(component);
                yPos += component.getFinalHeight();
            }
        }

        this.height = yPos - getPosY();
    }

    public void draw(int mouseX, int mouseY) {
        RenderUtil.drawRect(posX, posY, width, height, new Color(0, 0, 0, 180).getRGB());

        float yPos = getPosY();

        for(ValueComponent component : this.components) {
            if(!component.getValue().isVisible())
                continue;
            component.setPosX(posX);
            component.setPosY(yPos);
            float width = component.draw(mouseX, mouseY);
            if(this.width < width && width > defaultWidth)
                this.width = width;
            component.setWidth(this.width);
            yPos += component.getFinalHeight();
        }

        this.height = yPos - getPosY();

    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for(ValueComponent component : this.components) {
            if(!component.getValue().isVisible())
                continue;
            component.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public Module getModule() {
        return module;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

}
