package wtf.atani.screen.main.atani.page.impl;

import net.minecraft.client.gui.GuiScreen;
import wtf.atani.screen.main.atani.page.Page;

public abstract class SecondPage extends Page {

    private final String name;

    public SecondPage(GuiScreen guiScreen, String name, float pageX, float pageY, float pageWidth, float pageHeight, float screenWidth, float screenHeight) {
        super(guiScreen, pageX, pageY, pageWidth, pageHeight, screenWidth, screenHeight);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
