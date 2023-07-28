package wtf.atani.screen.main.atani.page;

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.Sys;
import wtf.atani.screen.main.atani.button.MenuButton;
import wtf.atani.utils.render.RenderUtil;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Page {

    protected float pageX, pageY, pageWidth, pageHeight;
    protected float screenWidth, screenHeight;
    protected final CopyOnWriteArrayList<MenuButton> menuButtons = new CopyOnWriteArrayList<>();
    public GuiScreen guiScreen;

    public Page(GuiScreen guiScreen, float pageX, float pageY, float pageWidth, float pageHeight, float screenWidth, float screenHeight) {
        this.guiScreen = guiScreen;
        this.pageX = pageX;
        this.pageY = pageY;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for(MenuButton menuButton : menuButtons) {
            if(RenderUtil.isHovered(mouseX, mouseY, menuButton.getPosX(), menuButton.getPosY() + menuButton.scroll, menuButton.getWidth(), menuButton.getHeight())) {
                menuButton.getAction().run();
            }
        }
    }

    public void update(float screenWidth, float screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public abstract void draw(int mouseX, int mouseY);

    public abstract void refresh();

}
