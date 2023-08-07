package wtf.atani.screen.click.astolfo.window;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import wtf.atani.module.Module;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.screen.click.astolfo.frame.Frame;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public class Window extends Button {

    public ArrayList<Frame> moduleButtons = new ArrayList<>();

    public Color color;
    public Category category;

    public int mouseX2, mouseY2;
    public int count;

    public Window(float x, float y, float width, float height, Category category, Color color) {
        this.category = category;
        this.color = new Color(255,255,255);

        int count = 0;

        final float startModuleY = y + height;
        for(Module module : ModuleStorage.getInstance().getModules(category)){
            if(module.getCategory() == category) {
                moduleButtons.add(new Frame(x, startModuleY + height * count, width, height, module, color));
                count++;
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY) {
        Gui.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xff181A17);

        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(category.getName().toLowerCase(Locale.ROOT), getX() + 4.5f, (float) (getY() + getHeight() / 2 - 2.5), 0xffffffff);

        count = 0;

        final float start = getY() + getHeight();

        for(Frame moduleButton : moduleButtons) {
            moduleButton.x = getX();
            moduleButton.y = start + count;
            moduleButton.drawScreen(mouseX, mouseY);
            count += moduleButton.finalHeight;
        }

        Gui.drawRect(getX(), (getY() + count) + getHeight(), getX() + getWidth(), (getY() + count) + getHeight() + 2, 0xff181A17);
        drawAstolfoBorderedRect(getX(), getY(), getX() + getWidth(), (getY() + count) + getHeight() + 2, 1.2f, color.getRGB());
    }

    public static void drawAstolfoBorderedRect(float left, float top, float right, float bottom, float thickness, int color) {
        Gui.drawRect(left - thickness, top, left, bottom + 1.f, color);
        Gui.drawRect(right, top, right + thickness, bottom + 1.f, color);
        Gui.drawRect(left, top + thickness, right, top, color);
        Gui.drawRect(left, bottom, right, bottom + thickness, color);
    }
}
