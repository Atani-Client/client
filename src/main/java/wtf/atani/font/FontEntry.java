package wtf.atani.font;

import wtf.atani.font.renderer.CustomFontRenderer;

public class FontEntry {

    private final String name;
    private final float size;
    private final CustomFontRenderer fontRenderer;

    public FontEntry(String name, float size, CustomFontRenderer fontRenderer) {
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

    public CustomFontRenderer getFontRenderer() {
        return fontRenderer;
    }
}
