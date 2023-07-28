package wtf.atani.screen.main.atani.page.impl.second;

import net.minecraft.client.gui.*;
import net.minecraft.client.gui.stream.GuiStreamOptions;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.button.MenuButton;
import wtf.atani.screen.main.atani.button.impl.ServerButton;
import wtf.atani.screen.main.atani.button.impl.SimpleButton;
import wtf.atani.screen.main.atani.page.impl.SecondPage;
import wtf.atani.utils.interfaces.Methods;

public class OptionsPage extends SecondPage implements Methods {
    public OptionsPage(GuiScreen guiScreen, float pageX, float pageY, float pageWidth, float pageHeight, float screenWidth, float screenHeight) {
        super(guiScreen, "Options", pageX, pageY, pageWidth, pageHeight, screenWidth, screenHeight);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        for(MenuButton menuButton : this.menuButtons) {
            if(!(menuButton instanceof ServerButton)) {
                menuButton.draw(mouseX, mouseY);
            }
        }
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
        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
        float usableAreaX = pageX + 1;
        float usableAreaY = pageY + 7 * 2 + bigger.FONT_HEIGHT;
        float usableAreaWidth = pageWidth - 2;
        float usableAreaHeight = pageHeight - (7 * 2 + bigger.FONT_HEIGHT) - (7 * 2);
        this.menuButtons.add(new SimpleButton("Skin Customization...", usableAreaX, usableAreaY, usableAreaWidth, () -> this.mc.displayGuiScreen(new GuiCustomizeSkin(guiScreen))));
        this.menuButtons.add(new SimpleButton("Music & Sounds...", usableAreaX, usableAreaY + 15 * 1, usableAreaWidth, () -> this.mc.displayGuiScreen(new GuiScreenOptionsSounds(guiScreen, mc.gameSettings))));
        this.menuButtons.add(new SimpleButton("Video Settings...", usableAreaX, usableAreaY + 15 * 2, usableAreaWidth, () -> this.mc.displayGuiScreen(new GuiVideoSettings(guiScreen, mc.gameSettings))));
        this.menuButtons.add(new SimpleButton("Language...", usableAreaX, usableAreaY + 15 * 3, usableAreaWidth, () -> this.mc.displayGuiScreen(new GuiLanguage(guiScreen, mc.gameSettings, this.mc.getLanguageManager()))));
        this.menuButtons.add(new SimpleButton("Resource Packs...", usableAreaX, usableAreaY + 15 * 4, usableAreaWidth, () -> this.mc.displayGuiScreen(new GuiScreenResourcePacks(guiScreen))));
        this.menuButtons.add(new SimpleButton("Super Secret Settings...", usableAreaX, usableAreaY + 15 * 5, usableAreaWidth, () -> this.mc.entityRenderer.activateNextShader()));
        this.menuButtons.add(new SimpleButton("Broadcast Settings...", usableAreaX, usableAreaY + 15 * 6, usableAreaWidth, () -> this.mc.displayGuiScreen(new GuiStreamOptions(guiScreen, mc.gameSettings))));
        this.menuButtons.add(new SimpleButton("Controls...", usableAreaX, usableAreaY + 15 * 7, usableAreaWidth, () -> this.mc.displayGuiScreen(new GuiControls(guiScreen, mc.gameSettings))));
        this.menuButtons.add(new SimpleButton("Chat Settings...", usableAreaX, usableAreaY + 15 * 8, usableAreaWidth, () -> this.mc.displayGuiScreen(new ScreenChatOptions(guiScreen, mc.gameSettings))));
        this.menuButtons.add(new SimpleButton("Snooper Settings...", usableAreaX, usableAreaY + 15 * 9, usableAreaWidth, () -> this.mc.displayGuiScreen(new GuiSnooper(guiScreen, mc.gameSettings))));
    }
}
