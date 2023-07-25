package wtf.atani.screen.click.simple.component;

import java.util.ArrayList;

public abstract class Component {

    protected final ArrayList<Component> subComponents = new ArrayList<>();
    private float posX, posY, baseWidth, baseHeight;
    private int scroll;
    private boolean visible = true;

    public Component(float posX, float posY, float baseWidth, float baseHeight) {
        this.posX = posX;
        this.posY = posY;
        this.baseWidth = baseWidth;
        this.baseHeight = baseHeight;
    }

    public abstract void drawScreen(int mouseX, int mouseY);
    public abstract void mouseClick(int mouseX, int mouseY, int mouseButton);

    public float getFinalHeight() {
        return this.baseHeight;
    }

    public float getFinalWidth() {
        return this.baseWidth;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public float getBaseWidth() {
        return baseWidth;
    }

    public float getBaseHeight() {
        return baseHeight;
    }

    public int getScroll() {
        return scroll;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public void setBaseWidth(float baseWidth) {
        this.baseWidth = baseWidth;
    }

    public void setBaseHeight(float baseHeight) {
        this.baseHeight = baseHeight;
    }

    public void setScroll(int scroll) {
        this.scroll = scroll;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
