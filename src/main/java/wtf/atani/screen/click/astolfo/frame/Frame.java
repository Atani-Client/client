package wtf.atani.screen.click.astolfo.frame;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.screen.click.astolfo.frame.component.Component;
import wtf.atani.screen.click.astolfo.frame.component.impl.BooleanButton;
import wtf.atani.screen.click.astolfo.frame.component.impl.ModeButton;
import wtf.atani.screen.click.astolfo.frame.component.impl.NumberButton;
import wtf.atani.value.Value;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;
import wtf.atani.value.storage.ValueStorage;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

import static wtf.atani.utils.interfaces.Methods.mc;

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

        int count = 0;
        for(Value value : ValueStorage.getInstance().getValues(mod)) {
            if(value instanceof StringBoxValue)
                buttons.add(new ModeButton(x, startModuleHeight + 18 * count, width, 9f, (StringBoxValue) value, color));
            if(value instanceof CheckBoxValue)
                buttons.add(new BooleanButton(x, startModuleHeight + 18 * count, width, 9f, (CheckBoxValue) value, color));
            if(value instanceof SliderValue)
                buttons.add(new NumberButton(x, startModuleHeight + 18 * count, width, 9f, (SliderValue) value, color));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Gui.drawRect(x, y, x + width, y + height, 0xff181A17);

        if(expanded)
            Gui.drawRect(x + 2, y, x + width - 2, y + height, 0xff181A17);
        else
            Gui.drawRect(x + 2, y, x + width - 2, y + height, module.isEnabled() ? hovered ? color.darker().getRGB() : color.getRGB() : hovered ? new Color(0xff232623).darker().getRGB() : 0xff232623);

        fontRenderer.drawCenteredStringWithShadow(module.getName().toLowerCase(Locale.ROOT),
                x + width - Minecraft.getMinecraft().fontRendererObj.getStringWidth(module.getName().toLowerCase(Locale.ROOT)) - 4, y + height / 2 + 1.5f, expanded ? module.isEnabled() ? color.getRGB() : 0xffffffff : 0xffffffff);

        if(module.getKey() != 0 && !module.isListening()) {
            fontRenderer.drawCenteredStringWithShadow("[" + Keyboard.getKeyName(module.getKey()) + "]".toUpperCase(), x + width - 95, y + height / 2 + 1.5f, new Color(73, 75, 85).getRGB());
        }else if(module.isListening()) {
            fontRenderer.drawCenteredStringWithShadow("[...]".toUpperCase(), x + width - 95, y + height / 2 + 1.5f, new Color(73, 75, 85).getRGB());
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
        if(isHovered(mouseX, mouseY) && click) {
            if(button == 0) {
                module.toggle();
            }else if(button == 1) {
                expanded = !expanded;
            }else {
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
