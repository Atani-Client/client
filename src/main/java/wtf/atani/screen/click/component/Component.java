package wtf.atani.screen.click.component;

import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

public abstract class Component {
    protected ArrayList<Component> subComponents = new ArrayList<>();


    public abstract void drawScreen(int mouseX, int mouseY, float partialTicks);

    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);

    public abstract float getFinalHeight();

    public ArrayList<Component> getSubComponents() {
        return subComponents;
    }

}
