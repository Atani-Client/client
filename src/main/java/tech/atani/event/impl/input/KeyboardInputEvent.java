package tech.atani.event.impl.input;

import tech.atani.event.Event;
import net.minecraft.client.gui.GuiScreen;

public final class KeyboardInputEvent implements Event {
    private final int keyCode;
    private final GuiScreen guiScreen;

    public KeyboardInputEvent(int keyCode, GuiScreen guiScreen) {
        this.keyCode = keyCode;
        this.guiScreen = guiScreen;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public GuiScreen getGuiScreen() {
        return guiScreen;
    }
}
