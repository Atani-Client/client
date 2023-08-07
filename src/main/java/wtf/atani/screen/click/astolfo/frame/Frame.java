package wtf.atani.screen.click.astolfo.frame;

import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import wtf.atani.module.Module;
import wtf.atani.screen.click.astolfo.frame.component.Component;
import wtf.atani.screen.click.astolfo.frame.component.impl.CheckboxComponent;
import wtf.atani.screen.click.astolfo.frame.component.impl.ModeComponent;
import wtf.atani.screen.click.astolfo.frame.component.impl.SliderComponent;
import wtf.atani.value.Value;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;
import wtf.atani.value.storage.ValueStorage;

import java.awt.*;
import java.util.ArrayList;

public class Frame extends Component {

    public ArrayList<Component> buttons = new ArrayList<>();

    public Module module;
    public Color color;

    public boolean hovered;
    public float finalHeight;

    public boolean expanded = false;

    public Frame(float x, float y, float width, float height, Module mod, Color color) {
        super(x, y, width, height);

        module = mod;
        this.color = color;

        final float startModuleHeight = y + height;

        for(Value value : ValueStorage.getInstance().getValues(mod)) {
            if (value instanceof StringBoxValue) {
                buttons.add(new ModeComponent(x, startModuleHeight + 18, width, 9f, (StringBoxValue) value, color));
            } else if (value instanceof CheckBoxValue) {
                buttons.add(new CheckboxComponent(x, startModuleHeight + 18, width, 9f, (CheckBoxValue) value, color));
            } else if (value instanceof SliderValue) {
                buttons.add(new SliderComponent(x, startModuleHeight + 18, width, 9f, (SliderValue) value, color));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Gui.drawRect(x, y, x + width, y + height, new Color(25,25,25).getRGB());

        if(expanded)
            Gui.drawRect(x + 2, y, x + width - 2, y + height, new Color(25,25,25).getRGB());
        else
            Gui.drawRect(x + 2, y, x + width - 2, y + height, module.isEnabled() ? hovered ? color.darker().getRGB() : color.getRGB() : hovered ? new Color(35,35,35).darker().getRGB() : new Color(35,35,35).getRGB());

        fontRenderer.drawHeightCenteredString(module.getName().toLowerCase(),
                x + width - fontRenderer.getStringWidth(module.getName().toLowerCase()) - 4, y + height / 2 + 1.5f, expanded ? module.isEnabled() ? color.getRGB() : -1 : -1);

        if(module.getKey() != 0 && !module.isListening()) {
            fontRenderer.drawHeightCenteredString("[" + Keyboard.getKeyName(module.getKey()) + "]".toUpperCase(), x + width - 95, y + height / 2 + 1.5f, new Color(75, 75, 75).getRGB());
        }else if(module.isListening()) {
            fontRenderer.drawHeightCenteredString("[...]".toUpperCase(), x + width - 95, y + height / 2 + 1.5f, new Color(75, 75, 75).getRGB());
        }

        float sexyMethod = 0;
        int count = 0;

        if(expanded) {
            final float startY = y + height;
            for(Component button : buttons) {
                button.x = x;
                button.y = startY + button.height * count;
                button.drawScreen(mouseX, mouseY);
                count++;
                sexyMethod = button.height;
            }
        }

        finalHeight = sexyMethod * count + height;
    }

    @Override
    public void actionPerformed(int mouseX, int mouseY, boolean click, int button) {
        if (isHovered(mouseX, mouseY) && click) {
            if (button == 0) {
                module.toggle();
            } else if(button == 1) {
                if(!buttons.isEmpty())
                    expanded = !expanded;
            } else {
                module.setListening(!module.isListening());
            }
        }
    }

    @Override
    public void key(char typedChar, int key) {
        if (module.isListening()) {
            this.module.setKey(key);

            if(key == 1){
                module.setKey(0);
            }

            module.setListening(false);
        }
    }
}
