package wtf.atani.screen.click;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.Sys;
import wtf.atani.module.data.enums.Category;
import wtf.atani.screen.click.component.frame.Frame;

import java.io.IOException;
import java.util.ArrayList;

public class ClickGuiScreen extends GuiScreen {

    private ArrayList<Frame> frames = new ArrayList<>();

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if(frames.isEmpty()) {
            float y = 50, width = 130, height = 20, x = (this.width - (Category.values().length * (width + 5))) / 2; /* The X is made like that so categories will be in the middle*/
            for(Category category : Category.values()) {
                this.frames.add(new Frame(category, x, y, width, height));
                x += width + 5;
            }
        }

        for(Frame frame : frames) {
            frame.drawScreen(mouseX, mouseY, partialTicks);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for(Frame frame : frames) {
            frame.mouseClicked(mouseX, mouseY, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}
