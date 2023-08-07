package wtf.atani.screen.click.astolfo.window;

import net.minecraft.client.gui.Gui;
import wtf.atani.module.Module;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.screen.click.astolfo.frame.Frame;
import wtf.atani.screen.click.astolfo.frame.component.Component;
import wtf.atani.utils.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

public class Window extends Component {

    public ArrayList<Frame> moduleButtons = new ArrayList<>();

    public Color color;
    public Category category;

    public int count;

    public Window(float x, float y, float width, float height, Category category, Color color) {
        super(x, y, width, height);
        this.category = category;
        this.color = color;

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
        Gui.drawRect(x, y, x + width, y + height, 0xff181A17);

        fontRenderer.drawString(category.getName().toLowerCase(), x + 4.5f, (float) (y + height / 2 - 2.5), 0xffffffff);

        count = 0;

        final float start = y + height;

        for(Frame moduleButton : moduleButtons) {
            moduleButton.x = x;
            moduleButton.y = start + count;
            moduleButton.drawScreen(mouseX, mouseY);
            count += moduleButton.finalHeight;
        }

        Gui.drawRect(x, (y + count) + height, x + width, (y + count) + height + 2, 0xff181A17);
        RenderUtil.drawAstolfoBorderedRect(x, y, x + width, (y + count) + height + 2, 1.2f, color.getRGB());
    }

    @Override
    public void actionPerformed(int x, int y, boolean click, int button) {}

    @Override
    public void key(char typedChar, int key) {}
}
