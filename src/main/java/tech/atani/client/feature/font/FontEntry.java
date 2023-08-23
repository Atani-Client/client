package tech.atani.client.feature.font;

import net.minecraft.client.gui.FontRenderer;

public class FontEntry {

    private final String name;
    private final float size;
    private final FontRenderer fontRenderer;

    public FontEntry(String name, float size, FontRenderer fontRenderer) {
        this.name = name;
        this.size = size;
        this.fontRenderer = fontRenderer;
    }

    public String getName() {
        return name;
    }

    public float getSize() {
        return size;
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }
}
