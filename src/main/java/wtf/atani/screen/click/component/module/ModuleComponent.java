package wtf.atani.screen.click.component.module;

import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.screen.click.component.Component;
import wtf.atani.screen.click.component.settings.CheckBoxComponent;
import wtf.atani.screen.click.component.settings.SliderComponent;
import wtf.atani.screen.click.component.settings.StringBoxComponent;
import wtf.atani.screen.click.component.settings.ValueComponent;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.value.Value;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;
import wtf.atani.value.storage.ValueStorage;

public class ModuleComponent extends Component {

    private Module module;
    private float posX, posY, width, elementHeight;
    private boolean expanded = false;

    public ModuleComponent(Module module, float posX, float posY, float width, float elementHeight) {
        this.module = module;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.elementHeight = elementHeight;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        FontStorage.getInstance().findFont("Roboto", 19).drawTotalCenteredStringWithShadow(module.getName(), posX + width / 2, posY + elementHeight / 2, -1);
        float y = this.posY + this.elementHeight;
        if(this.expanded) {
            for(Value value : ValueStorage.getInstance().getValues(module)) {
                boolean found = false;
                ValueComponent foundComponent = null;
                for(Component component : this.subComponents) {
                    ValueComponent valueComponent = (ValueComponent) component;
                    if(valueComponent.getValue() == value) {
                        found = true;
                        foundComponent = valueComponent;
                    }
                }
                if(found) {
                    if(!value.isVisible()) {
                        this.subComponents.remove(foundComponent);
                    }
                } else {
                    Component component = this.createComponent(value, posX, y, width, elementHeight);
                    this.subComponents.add(component);
                    y += component.getFinalHeight();
                }
            }
        } else {
            this.subComponents.clear();
        }
        for(Component component : this.subComponents) {
            component.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if(RenderUtil.isHovered(mouseX, mouseY, posX, posY, width, elementHeight)) {
            if(mouseButton == 0) {
                module.toggle();
            } else {
                expanded = !expanded;
                if(!this.expanded) {
                    this.subComponents.clear();
                }
            }
        }
        if(this.expanded) {
            for(Component component : this.subComponents) {
                component.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    private ValueComponent createComponent(Value value, float posX, float posY, float width, float height) {
        if(value instanceof CheckBoxValue) {
            CheckBoxComponent checkBoxComponent = new CheckBoxComponent((CheckBoxValue) value, posX, posY, width, elementHeight);
            return checkBoxComponent;
        } else if(value instanceof StringBoxValue) {
            StringBoxComponent stringBoxComponent = new StringBoxComponent((StringBoxValue) value, posX, posY, width, elementHeight);
            return stringBoxComponent;
        } else if(value instanceof SliderValue) {
            SliderComponent sliderComponent = new SliderComponent((SliderValue) value, posX, posY, width, elementHeight);
            return sliderComponent;
        }
        return null;
    }

    @Override
    public float getFinalHeight() {
        float height = elementHeight;
        for(Component component : this.subComponents) {
            height += component.getFinalHeight();
        }
        return height;
    }
}
