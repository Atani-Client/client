package wtf.atani.screen.main.atani.page.impl;

import net.minecraft.client.gui.FontRenderer;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.AtaniMainMenu;
import wtf.atani.screen.main.atani.button.MenuButton;
import wtf.atani.screen.main.atani.button.impl.SimpleButton;
import wtf.atani.screen.main.atani.page.Page;
import wtf.atani.utils.interfaces.ClientInformationAccess;

import java.awt.*;
import java.util.ArrayList;

public class FirstPage extends Page implements ClientInformationAccess {

    private final AtaniMainMenu parent;
    private String selected = "ChangeLog";

    public FirstPage(AtaniMainMenu parent, float pageX, float pageY, float pageWidth, float pageHeight, float screenWidth, float screenHeight) {
        super(parent, pageX, pageY, pageWidth, pageHeight, screenWidth, screenHeight);
        this.parent = parent;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        float sixthY = this.screenHeight / 6F;
        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);

        // Logo
        FontStorage.getInstance().findFont("Android 101", 100).drawTotalCenteredStringWithShadow(CLIENT_NAME.toLowerCase(), pageX + pageWidth / 2, pageY + sixthY / 3, -1);
        for(MenuButton menuButton : this.menuButtons) {
            menuButton.draw(mouseX, mouseY);
        }
        // Version
        bigger.drawCenteredString(String.format("Running version %s", VERSION), pageX + pageWidth / 2, pageY + pageHeight - 4 - 10, new Color(80, 80, 80).getRGB());
        bigger.drawCenteredString(String.format("%s Version", "Premium"), pageX + pageWidth / 2, pageY + pageHeight - 4 - bigger.FONT_HEIGHT - 2 - 10, new Color(80, 80, 80).getRGB());
    }

    public String getSelected(){
        return selected;
    }

    @Override
    public void refresh() {
        float sixthY = this.screenHeight / 6F;
        float fourthX = this.screenWidth/ 4F;
        this.pageX = fourthX - 7;
        this.pageY = sixthY * 0.5f;
        this.pageWidth = fourthX;
        this.pageHeight = sixthY * 3;
        this.menuButtons.clear();
        float buttonY = this.pageY + (sixthY / 3 * 2);
        ArrayList<String> buttons = new ArrayList<>();
        buttons.add("SinglePlayer");
        buttons.add("MultiPlayer");
        buttons.add("Options");
        buttons.add("Account Manager");
        buttons.add("Client Settings");
        buttons.add("License");
        for(String button : buttons) {
            this.menuButtons.add(new SimpleButton(button, pageX + 1, buttonY, pageWidth - 2, () ->  selected = button));
            buttonY += 15;
        }
    }
}
