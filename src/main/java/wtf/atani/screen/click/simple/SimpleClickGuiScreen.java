package wtf.atani.screen.click.simple;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.impl.hud.ClickGui;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.screen.click.simple.frame.Frame;
import wtf.atani.utils.render.shader.render.ingame.RenderableShaders;

import java.io.IOException;
import java.util.ArrayList;

public class SimpleClickGuiScreen extends GuiScreen {
    private ArrayList<Frame> frames = new ArrayList<>();

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width - 60, this.height - 25, 55, 20, "Reset Gui"));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(frames.isEmpty()) {
            float y = 50, width = 130, height = 15, x = (this.width - (Category.values().length * (width + 5))) / 2; /* The X is made like that so categories will be in the middle*/
            for(Category category : Category.values()) {
                this.frames.add(new Frame(category, x, y, width, height, height));
                x += width + 5;
            }
        }

        RenderableShaders.renderAndRun(() -> {
            for(Frame frame : frames) {
                frame.drawScreen(mouseX, mouseY);
            }
        });
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id){
            case 0:
                mc.displayGuiScreen(null);
                ClickGui.clickGuiScreen = null;
                ModuleStorage.getInstance().getByClass(ClickGui.class).toggle();
                break;
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(Frame frame : frames) {
            frame.mouseClick(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
