package wtf.atani.screen.click.simple.frame;

import net.minecraft.client.gui.FontRenderer;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.screen.click.simple.component.Component;
import wtf.atani.screen.click.simple.component.impl.ModuleComponent;
import wtf.atani.utils.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Frame extends Component {

    private final Category category;
    private final float moduleHeight;

    public Frame(Category category, float posX, float posY, float width, float height, float moduleHeight) {
        super(posX, posY, width, height);
        this.category = category;
        this.moduleHeight = moduleHeight;

        // The Y position in here is basically useless as the actual Y pos is overwritten in drawScreen
        float moduleY = posY + height;
        ArrayList<Module> modules = ModuleStorage.getInstance().getModules(this.category);
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto", 19);
        modules.sort(Comparator.comparingInt(o -> normal.getStringWidth(((Module)o).getName())).reversed());
        for(Module module : modules) {
            this.subComponents.add(new ModuleComponent(module, posX, moduleY, width, moduleHeight));
            moduleY += moduleHeight;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RenderUtil.drawRect(getPosX() + getAddX(), getPosY() + getAddY(), getBaseWidth(), getBaseHeight(), new Color(0, 0, 0, 180).getRGB());
        FontStorage.getInstance().findFont("Roboto", 19).drawTotalCenteredStringWithShadow(category.getName(), this.getPosX() + this.getBaseWidth() / 2 + getAddX(), this.getPosY() + this.getBaseHeight() / 2 + getAddY(), -1);
        float moduleY = this.getPosY() + this.getBaseHeight();
        for(Component component : this.subComponents) {
            if(component instanceof ModuleComponent) {
                component.setPosX(component.getPosX());
                component.setPosY(moduleY + getAddY());
                component.setAddX(this.getAddX());
                component.drawScreen(mouseX, mouseY);
                moduleY += component.getFinalHeight();
            }
        }
    }

    @Override
    public float getFinalHeight() {
        float totalComponentHeight = 0;
        for(Component component : this.subComponents) {
            totalComponentHeight += component.getFinalHeight();
        }
        return this.getBaseHeight() + totalComponentHeight;
    }

    @Override
    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
        float moduleY = this.getPosY() + this.getBaseHeight();
        for(Component component : this.subComponents) {
            if(component instanceof ModuleComponent) {
                component.setPosY(moduleY + getAddY());
                component.mouseClick(mouseX, mouseY, mouseButton);
                moduleY += component.getFinalHeight();
            }
        }
    }

}
