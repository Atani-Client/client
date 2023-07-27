package wtf.atani.screen.main.atani.page.impl.second;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.AtaniMainMenu;
import wtf.atani.screen.main.atani.button.MenuButton;
import wtf.atani.screen.main.atani.page.Page;
import wtf.atani.screen.main.atani.page.impl.SecondPage;

import java.awt.*;
import java.util.ArrayList;

public class LicensePage extends SecondPage {
    public LicensePage(GuiScreen guiScreen, float pageX, float pageY, float pageWidth, float pageHeight, float screenWidth, float screenHeight) {
        super(guiScreen, "License", pageX, pageY, pageWidth, pageHeight, screenWidth, screenHeight);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        float sixthY = this.screenHeight / 6F;
        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
        float usableAreaX = pageX + 1;
        float usableAreaY = pageY + 7 * 2 + bigger.FONT_HEIGHT;
        float usableAreaWidth = pageWidth - 2;
        float usableAreaHeight = pageHeight - (7 * 2 + bigger.FONT_HEIGHT) - (7 * 2);
        bigger.drawStringWithShadow(String.format("License: %s", "Premium"), usableAreaX + 10, usableAreaY, -1);
        bigger.drawStringWithShadow(String.format("User: %s", "Idk"), usableAreaX + 10, usableAreaY + bigger.FONT_HEIGHT + 3, -1);
    }

    @Override
    public void refresh() {
        float sixthY = this.screenHeight / 6F;
        float fourthX = this.screenWidth/ 4F;
        this.pageX = fourthX * 2 + 7;
        this.pageY = sixthY * 0.5f;
        this.pageWidth = fourthX;
        this.pageHeight = sixthY * 3;
        this.menuButtons.clear();
    }
}
