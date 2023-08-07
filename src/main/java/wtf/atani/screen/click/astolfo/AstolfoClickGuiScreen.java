package wtf.atani.screen.click.astolfo;

import net.minecraft.client.gui.FontRenderer;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.data.enums.Category;
import wtf.atani.screen.click.astolfo.frame.Frame;
import net.minecraft.client.gui.GuiScreen;
import wtf.atani.screen.click.astolfo.frame.component.Component;
import wtf.atani.screen.click.astolfo.window.Window;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class AstolfoClickGuiScreen extends GuiScreen {

    public ArrayList<wtf.atani.screen.click.astolfo.window.Window> categoryPanels = new ArrayList<>();

    public AstolfoClickGuiScreen() {
        int count = 4;
        for(Category category : Category.values()) {
            categoryPanels.add(new wtf.atani.screen.click.astolfo.window.Window(count, 4, 100, 18, category, new Color(255,255,255)));
            count += 120;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (wtf.atani.screen.click.astolfo.window.Window categoryPanel : categoryPanels){
            categoryPanel.drawScreen(mouseX, mouseY);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(wtf.atani.screen.click.astolfo.window.Window categoryPanel : categoryPanels){
            if(categoryPanel.category != null) {
                for(Frame moduleButton : categoryPanel.moduleButtons) {
                    moduleButton.actionPerformed(mouseX, mouseY, true, mouseButton);
                    if(moduleButton.expanded) {
                        for(Component panel : moduleButton.buttons) {
                            panel.actionPerformed(mouseX, mouseY, true, mouseButton);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for(wtf.atani.screen.click.astolfo.window.Window categoryPanel : categoryPanels){
            for(Frame moduleButton : categoryPanel.moduleButtons) {
                moduleButton.actionPerformed(mouseX, mouseY, false, state);
                if(moduleButton.expanded) {
                    for(Component panel : moduleButton.buttons) {
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
        for (Window catPanel : categoryPanels) {
            for (Frame modPan : catPanel.moduleButtons) {
                modPan.key(typedChar, keyCode);
            }
        }

        if(keyCode == 1) {
            mc.currentScreen = null;
            this.mc.setIngameFocus();
        }
    }
}
