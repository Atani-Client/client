package wtf.atani.screen.main.atani.page.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.button.impl.TextField;

public class RenamePage extends SecondPage {

    private TextField textField;

    public RenamePage(GuiScreen guiScreen, String name, float pageX, float pageY, float pageWidth, float pageHeight, float screenWidth, float screenHeight) {
        super(guiScreen, name, pageX, pageY, pageWidth, pageHeight, screenWidth, screenHeight);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        this.textField.draw(mouseX, mouseY);
    }

    @Override
    public void refresh() {
        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
        float sixthY = this.screenHeight / 6F;
        float fourthX = this.screenWidth/ 4F;
        this.pageX = fourthX * 2 + 7;
        this.pageY = sixthY * 0.5f;
        this.pageWidth = fourthX;
        this.pageHeight = sixthY * 3;
        this.menuButtons.clear();
        float usableAreaX = pageX + 1;
        float usableAreaY = pageY + 7 * 2 + bigger.FONT_HEIGHT;
        float usableAreaWidth = pageWidth - 2;
        float usableAreaHeight = pageHeight - (7 * 2 + bigger.FONT_HEIGHT) - (7 * 2);
        float buttonY = usableAreaY;
        this.menuButtons.add(textField = new TextField(this.getName(), "", usableAreaX + usableAreaWidth / 2, usableAreaY + usableAreaHeight - 30, usableAreaWidth / 2, 15));
    }
}
