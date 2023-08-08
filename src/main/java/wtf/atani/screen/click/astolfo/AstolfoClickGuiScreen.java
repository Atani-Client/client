package wtf.atani.screen.click.astolfo;

import wtf.atani.module.data.enums.Category;
import wtf.atani.screen.click.astolfo.frame.Frame;
import net.minecraft.client.gui.GuiScreen;
import wtf.atani.screen.click.astolfo.frame.component.Component;
import wtf.atani.screen.click.astolfo.window.Window;
import wtf.atani.utils.render.color.ColorUtil;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class AstolfoClickGuiScreen extends GuiScreen {

    public ArrayList<Window> categoryPanels = new ArrayList<>();

    public AstolfoClickGuiScreen() {
        int count = 4;
        int counter = 0;
        for(Category category : Category.values()) {
            categoryPanels.add(new Window(count, 4, 100, 18, category, new Color(ColorUtil.blendRainbowColours(counter * 150L))));
            count += 120;
            counter++;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (Window categoryPanel : categoryPanels){
            categoryPanel.drawScreen(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(Window categoryPanel : categoryPanels){
            if(categoryPanel.category != null) {
                for(Frame moduleButton : categoryPanel.moduleButtons) {
                    moduleButton.actionPerformed(mouseX, mouseY, true, mouseButton);
                    if(moduleButton.expanded) {
                        for(Component panel : moduleButton.components) {
                            panel.actionPerformed(mouseX, mouseY, true, mouseButton);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for(Window categoryPanel : categoryPanels){
            for(Frame moduleButton : categoryPanel.moduleButtons) {
                moduleButton.actionPerformed(mouseX, mouseY, false, state);
                if(moduleButton.expanded) {
                    for(Component panel : moduleButton.components) {
                        panel.actionPerformed(mouseX, mouseY, false, state);
                    }
                }
            }
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        for (Window categoryPanel : categoryPanels) {
            for (Frame modulePanel : categoryPanel.moduleButtons) {
                modulePanel.keyTyped(typedChar, keyCode);
            }
        }

        if(keyCode == 1) {
            mc.currentScreen = null;
            this.mc.setIngameFocus();
        }
    }
}
