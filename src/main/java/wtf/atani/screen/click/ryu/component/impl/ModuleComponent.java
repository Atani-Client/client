package wtf.atani.screen.click.ryu.component.impl;

import net.minecraft.client.gui.FontRenderer;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.screen.click.ryu.component.Component;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.value.Value;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;
import wtf.atani.value.storage.ValueStorage;

import java.awt.*;

public class ModuleComponent extends Component {

    private final Module module;
    private boolean expanded = false;

    public ModuleComponent(Module module, float posX, float posY, float width, float height) {
        super(posX, posY, width, height);
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto", 19);
        normal.drawTotalCenteredString(module.getName(), getPosX() + getBaseWidth() / 2 + getAddX(), getPosY() + getBaseHeight() / 2, !module.isEnabled() ? -1 : RYU);
        if(expanded && this.subComponents.isEmpty()) {
            for(Value value : ValueStorage.getInstance().getValues(module)) {
                float valueY = this.getPosY() + this.getBaseHeight();
                if(value instanceof CheckBoxValue) {
                    CheckBoxComponent component = new CheckBoxComponent(value, this.getPosX(), valueY, this.getBaseWidth(), this.getBaseHeight() + 2);
                    this.subComponents.add(component);
                    valueY += component.getFinalHeight();
                } else if(value instanceof StringBoxValue) {
                    StringBoxComponent component = new StringBoxComponent(value, this.getPosX(), valueY, this.getBaseWidth(), this.getBaseHeight() + 2);
                    this.subComponents.add(component);
                    valueY += component.getFinalHeight();
                } else if(value instanceof SliderValue) {
                    SliderComponent component = new SliderComponent(value, this.getPosX(), valueY, this.getBaseWidth(), this.getBaseHeight() + 2);
                    this.subComponents.add(component);
                    valueY += component.getFinalHeight();
                }
            }
        } else if(!this.expanded) {
            subComponents.clear();
        }
        if(expanded) {
            float valueY = this.getPosY() + this.getBaseHeight();
            for(Component component : this.subComponents) {
                if(component instanceof ValueComponent) {
                    ValueComponent valueComponent = (ValueComponent) component;
                    valueComponent.setAddX(this.getAddX());
                    valueComponent.setPosY(valueY);
                    Value value = valueComponent.getValue();
                    valueComponent.setVisible(value.isVisible());
                    if(!valueComponent.isVisible())
                        continue;
                    valueComponent.drawScreen(mouseX, mouseY);
                    valueY += valueComponent.getFinalHeight();
                }
            }
        }
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovered(mouseX, mouseY, this.getPosX(), this.getPosY(), this.getBaseWidth(), this.getBaseHeight())) {
            switch (mouseButton) {
                case 0:
                    module.toggle();
                    break;
                case 1:
                    this.expanded = !this.expanded;
                    break;
            }
        }
        if(expanded) {
            float valueY = this.getPosY() + this.getBaseHeight();
            for(Component component : this.subComponents) {
                if(component instanceof ValueComponent) {
                    ValueComponent valueComponent = (ValueComponent) component;
                    valueComponent.setPosY(valueY);
                    Value value = valueComponent.getValue();
                    valueComponent.setVisible(value.isVisible());
                    if(!valueComponent.isVisible())
                        continue;
                    valueComponent.mouseClick(mouseX, mouseY, mouseButton);
                    valueY += valueComponent.getFinalHeight();
                }
            }
        }
    }

    @Override
    public float getFinalHeight() {
        float totalComponentHeight = 0;
        if(this.expanded) {
            for(Component component : this.subComponents) {
                totalComponentHeight += component.isVisible() ? component.getFinalHeight() : 0;
            }
        }
        return this.getBaseHeight() + totalComponentHeight;
    }

    public Module getModule() {
        return module;
    }
}
