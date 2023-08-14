package wtf.atani.screen.click.icarus.window;

import wtf.atani.module.Module;
import wtf.atani.screen.click.icarus.window.component.impl.CheckboxComponent;
import wtf.atani.screen.click.icarus.window.component.impl.ModeComponent;
import wtf.atani.screen.click.icarus.window.component.impl.SliderComponent;
import wtf.atani.screen.click.icarus.window.component.impl.ValueComponent;
import wtf.atani.utils.render.shader.legacy.shaders.RoundedShader;
import wtf.atani.value.Value;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;
import wtf.atani.value.storage.ValueStorage;

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
                CheckboxComponent component = new CheckboxComponent((CheckBoxValue) value, posX, yPos, 18);
                this.components.add(component);
                yPos += component.getFinalHeight();
            } else if(value instanceof SliderValue) {
                SliderComponent component = new SliderComponent((SliderValue) value, posX, yPos, 18);
                this.components.add(component);
                yPos += component.getFinalHeight();
            }else if(value instanceof StringBoxValue) {
                ModeComponent component = new ModeComponent((StringBoxValue) value, posX, yPos, 18);
                this.components.add(component);
                yPos += component.getFinalHeight();
            }
        }

        this.height = yPos - getPosY();
    }

    public void draw(int mouseX, int mouseY) {
        RoundedShader.drawRound(posX, posY, width, height, 5, new Color(20, 20, 20));

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
