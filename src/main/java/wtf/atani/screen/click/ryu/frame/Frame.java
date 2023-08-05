package wtf.atani.screen.click.ryu.frame;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.FontRenderer;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.screen.click.ryu.component.Component;
import wtf.atani.screen.click.ryu.component.impl.ModuleComponent;
import wtf.atani.utils.interfaces.ColorPalette;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.RoundedUtil;
import wtf.atani.utils.render.shader.render.ingame.RenderableShaders;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Frame extends Component implements ColorPalette {

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
        float moduleY = this.getPosY() + this.getBaseHeight();
        for(Component component : this.subComponents) {
            if(component instanceof ModuleComponent) {
                component.setPosX(component.getPosX());
                component.setPosY(moduleY + getAddY());
                component.setAddX(this.getAddX());
                //component.drawScreen(mouseX, mouseY);
                moduleY += component.getFinalHeight();
            }
        }
        final float finalY = moduleY;
        RenderableShaders.render(true, false, () -> {
            RoundedUtil.drawRoundOutline(getPosX() + getAddX(), getPosY() + getAddY(), getBaseWidth(), finalY - getPosY(), 7,  2, new Color(0, 0, 0, 0), new Color(0, 0, 0, 255));
        });
        RoundedUtil.drawRoundOutline(getPosX() + getAddX(), getPosY() + getAddY(), getBaseWidth(), moduleY - getPosY(), 7,  2, new Color(RYU), new Color(-1));
        RoundedUtil.drawRoundOutline(getPosX() + getAddX(), getPosY() + getAddY() + getBaseHeight(), getBaseWidth(), moduleY - getPosY() - getBaseHeight(), 7,  2, new Color(36, 37, 41), new Color(36, 37, 41, 0));
        FontRenderer medium24 = FontStorage.getInstance().findFont("Roboto Medium", 24);
        RenderableShaders.render(true, false, () -> {
            FontStorage.getInstance().findFont("Roboto", 24).drawString(category.getName(), this.getPosX() + 7.5f + getAddX(), this.getPosY() + 6 + getAddY() + 0.5f, new Color(0, 0, 0, 255).getRGB());
            FontStorage.getInstance().findFont("Roboto", 17).drawString("8", this.getPosX() + 7 + medium24.getStringWidth(category.getName()) + 2 + 0.5f + getAddX(), this.getPosY() + 6 + 0.5f + getAddY(), new Color(0, 0, 0, 255).getRGB());
        });
        medium24.drawString(category.getName(), this.getPosX() + 7 + getAddX(), this.getPosY() + 6 + getAddY(), -1);
        FontStorage.getInstance().findFont("Roboto Medium", 17).drawString(ChatFormatting.GRAY.toString() + ModuleStorage.getInstance().getModules(category).size() + "", this.getPosX() + 7 + medium24.getStringWidth(category.getName()) + 2 + getAddX(), this.getPosY() + 6 + getAddY(), -1);
        for(Component component : this.subComponents) {
            if(component instanceof ModuleComponent) {
                component.drawScreen(mouseX, mouseY);
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
