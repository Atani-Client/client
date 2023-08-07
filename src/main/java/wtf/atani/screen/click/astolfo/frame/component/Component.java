package wtf.atani.screen.click.astolfo.frame.component;

import net.minecraft.client.gui.FontRenderer;
import wtf.atani.font.storage.FontStorage;

public abstract class Component {

    public FontRenderer fontRenderer = FontStorage.getInstance().findFont("Arial", 16);

    public Component(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public float x, y, width, height;

    public abstract void drawScreen(int mouseX, int mouseY);

    public abstract void actionPerformed(int x, int y, boolean click, int button);
    public abstract void key(char typedChar, int key);

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY > y && mouseY < y + height;
    }
}
