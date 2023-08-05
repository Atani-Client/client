package wtf.atani.screen.click.icarus.frame;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.input.Mouse;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.screen.click.icarus.frame.component.Component;
import wtf.atani.screen.click.icarus.frame.component.impl.ModuleComponent;
import wtf.atani.utils.render.GradientUtil;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.RoundedUtil;
import wtf.atani.utils.render.shader.render.ingame.RenderableShaders;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Frame extends Component {

    private final Category category;
    private final float moduleHeight;
    private float draggingX, draggingY;
    private boolean dragging;

    public Frame(Category category, float posX, float posY, float width, float height, float moduleHeight) {
        super(posX, posY, width, height);
        this.category = category;
        this.moduleHeight = moduleHeight;

        // The Y position in here is basically useless as the actual Y pos is overwritten in drawScreen
        float moduleY = posY + height;
        ArrayList<Module> modules = ModuleStorage.getInstance().getModules(this.category);
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto", 16);
        modules.sort(Comparator.comparingInt(o -> normal.getStringWidth(((Module)o).getName())).reversed());
        for(Module module : modules) {
            this.subComponents.add(new ModuleComponent(module, posX + 2, moduleY, width - 4, moduleHeight));
            moduleY += moduleHeight;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(!Mouse.isButtonDown(0))
            dragging = false;
        if(dragging) {
            this.setPosY(mouseY - draggingY);
            this.setPosX(mouseX - draggingX);
        }
        float moduleY = this.getPosY() + this.getBaseHeight();
        for(Component component : this.subComponents) {
            if(component instanceof ModuleComponent) {
                component.setPosX(getPosX() + 2);
                component.setPosY(moduleY + getAddY());
                component.setAddX(this.getAddX());
                moduleY += component.getFinalHeight();
            }
        }
        RoundedUtil.drawRound(getPosX() + getAddX(), getPosY() + getAddY(), getBaseWidth(), moduleY - getPosY(), 5, new Color(20, 20, 20));
        FontRenderer fontRenderer = FontStorage.getInstance().findFont("Pangram Bold", 20);
        fontRenderer.drawString(category.getName(), getPosX() + 10 + getAddX(), getPosY() + getBaseHeight() / 2 - fontRenderer.FONT_HEIGHT / 2 - 1 + getAddY(), -1);
        GradientUtil.drawGradientLR(getPosX() + getAddX() - 1, getPosY() + getAddY() + getBaseHeight() - 0.5f, getBaseWidth() + 2, 1, 1, new Color(ICARUS_FIRST), new Color(ICARUS_SECOND));
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
        if (RenderUtil.isHovered(mouseX, mouseY, getPosX() + getAddX(), getPosY() + getAddY(), getBaseWidth(), getBaseHeight()) && mouseButton == 0) {
            dragging = true;
            draggingX = mouseX - getPosX();
            draggingY = mouseY - getPosY();
        }
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
