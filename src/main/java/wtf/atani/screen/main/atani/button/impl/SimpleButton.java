package wtf.atani.screen.main.atani.button.impl;

import wtf.atani.screen.main.atani.button.MenuButton;

public class SimpleButton extends MenuButton {
    public SimpleButton(String name, float posX, float posY, float width, Runnable action) {
        super(name, posX, posY, width, 15, action);
    }
}
