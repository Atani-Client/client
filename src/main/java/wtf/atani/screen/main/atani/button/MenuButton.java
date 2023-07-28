package wtf.atani.screen.main.atani.button;

import net.minecraft.client.gui.FontRenderer;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.animation.Direction;
import wtf.atani.utils.render.animation.impl.DecelerateAnimation;

import java.awt.*;

public class MenuButton {
    protected final String name;
    protected final float posX, posY, width, height;
    public int scroll = 0;
    protected Runnable action;
    protected DecelerateAnimation hoveringAnimation = new DecelerateAnimation(500, 1, Direction.BACKWARDS);

    public MenuButton(String name, float posX, float posY, float width, float height, Runnable action) {
        this.name = name;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
        this.action = action;
    }

    public void draw(int mouseX, int mouseY) {
        if (RenderUtil.isHovered(mouseX, mouseY, posX, posY + scroll, width, height)) {
            this.hoveringAnimation.setDirection(Direction.FORWARDS);
        } else {
            this.hoveringAnimation.setDirection(Direction.BACKWARDS);
        }
        RenderUtil.drawRect(posX, posY + scroll, width, height, new Color(255, 255, 255, (int) (20 * hoveringAnimation.getOutput())).getRGB());
        FontRenderer normal = FontStorage.getInstance().findFont("Roboto", 19);
        normal.drawTotalCenteredStringWithShadow(name, posX + width / 2, posY + height / 2 + scroll, -1);
    }

    public String getName() {
        return name;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Runnable getAction() {
        return action;
    }
}