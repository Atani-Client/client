package wtf.atani.screen.main.atani.button.impl;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.screen.main.atani.button.MenuButton;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.animation.Direction;
import wtf.atani.utils.render.animation.impl.DecelerateAnimation;

import java.awt.*;

public class TextField extends MenuButton {

    boolean typing = false;
    private String typed = "";
    private DecelerateAnimation typingAnimation = new DecelerateAnimation(100, 1, Direction.BACKWARDS);

    public TextField(String name, String typed, float posX, float posY, float width, float height) {
        super(name, posX, posY, width, height, null);
        typed = typed;
    }

    public void draw(int mouseX, int mouseY) {
        if(action == null)
            action = () -> typing = !typing;
        if(RenderUtil.isHovered(mouseX, mouseY, posX, posY, width, height) || typing) {
            this.hoveringAnimation.setDirection(Direction.FORWARDS);
        } else {
            this.hoveringAnimation.setDirection(Direction.BACKWARDS);
        }
        if(this.typingAnimation.isDone()) {
            this.typingAnimation.changeDirection();
        }
        RenderUtil.drawRect(posX, posY, width, height, new Color(255, 255, 255, (int) (20 * hoveringAnimation.getOutput())).getRGB());
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto", 19);
        normal.drawStringWithShadow(name + ": ", posX + 10, posY + height / 2 - normal.FONT_HEIGHT / 2, -1);
        normal.drawStringWithShadow("_", posX + 10 + normal.getStringWidth(name + ": "), posY + height / 2 - normal.FONT_HEIGHT / 2, (int) (this.typingAnimation.getOutput() * 255));
    }

    public void keyTyped(char typed, int keyCode) {
        if(typing) {
            if (keyCode == Keyboard.KEY_V && GuiScreen.isCtrlKeyDown())
                this.typed = GuiScreen.getClipboardString();
            if(keyCode == Keyboard.KEY_BACK && !this.typed.equals("")) {
                this.typed = this.typed.substring(0, this.typed.length()-1);
            } else if(keyCode != Keyboard.KEY_BACK && ChatAllowedCharacters.isAllowedCharacter(typed)) this.typed += typed;
            if (keyCode == Keyboard.KEY_RETURN) typing = false;
        }
    }

    public void mouseClick(int mouseX, int mouseY, int button) {
        if(RenderUtil.isHovered(mouseX, mouseY, posX, posY, width, height)) {
            typing = !typing;
        }
    }

    public String getTyped() {
        return typed;
    }

    public void setTyped(String typed) {
        this.typed = typed;
    }
}
