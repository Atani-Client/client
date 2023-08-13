package wtf.atani.font.storage;

import de.florianmichael.rclasses.storage.Storage;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import wtf.atani.font.FontEntry;
import wtf.atani.font.renderer.CustomFontRenderer;

import java.awt.*;

public class FontStorage extends Storage<FontEntry> {

    private static FontStorage instance;

    public FontStorage() {
        instance = this;
        init();
    }

    @Override
    public void init() {
        this.add(
                this.createFontEntry("Roboto", 14),
                this.createFontEntry("Roboto", 15),
                this.createFontEntry("Roboto", 16),
                this.createFontEntry("Roboto", 17),
                this.createFontEntry("Roboto", 18),
                this.createFontEntry("Roboto", 19),
                this.createFontEntry("Roboto", 20),
                this.createFontEntry("Roboto", 21),
                this.createFontEntry("Roboto", 24),
                this.createFontEntry("Roboto Medium", 15),
                this.createFontEntry("Roboto Medium", 16),
                this.createFontEntry("Roboto Medium", 17),
                this.createFontEntry("Roboto Medium", 18),
                this.createFontEntry("Roboto Medium", 19),
                this.createFontEntry("Roboto Medium", 20),
                this.createFontEntry("Roboto Medium", 21),
                this.createFontEntry("Roboto Medium", 24),
                this.createFontEntry("Tahoma", 16),
                this.createFontEntry("ESP", 80),
                this.createFontEntry("Pangram Bold", 80),
                this.createFontEntry("Pangram Bold", 20),
                this.createFontEntry("Pangram Regular", 16),
                this.createFontEntry("Pangram Regular", 17),
                this.createFontEntry("Product Sans", 17),
                this.createFontEntry("Product Sans", 18),
                this.createFontEntry("Product Sans", 19),
                this.createFontEntry("Product Sans", 20),
                this.createFontEntry("Product Sans", 21),
                this.createFontEntry("SFUI Medium", 16),
                this.createFontEntry("Arial", 17),
                this.createFontEntry("Arial", 18),
                this.createFontEntry("Arial", 19),
                this.createFontEntry("Arial", 20),
                this.createFontEntry("Arial", 21),
                this.createFontEntry("ArialMT", 16),
                this.createFontEntry("SF Pro Display Semibold", 17),
                this.createFontEntry("SF Pro Display Semibold", 18),
                this.createFontEntry("SF Pro Display Semibold", 19),
                this.createFontEntry("SF Pro Display Semibold", 20),
                this.createFontEntry("SF Pro Display Semibold", 21),
                this.createFontEntry("Android 101", 100, true));
    }

    public CustomFontRenderer findFont(String name, float size) {
        return this.getList().stream().filter(fontEntry -> fontEntry.getName().equalsIgnoreCase(name) && fontEntry.getSize() == size).findFirst().orElse(null).getFontRenderer();
    }

    private FontEntry createFontEntry(String name, float size) {
        return this.createFontEntry(name, size, false);
    }

    private FontEntry createFontEntry(String name, float size, boolean otf) {
        return new FontEntry(name, size, new CustomFontRenderer(this.getFontFromFile(new ResourceLocation(String.format("atani/%s.%s", name, otf ? "otf" : "ttf")), size, Font.PLAIN), true));
    }

    private Font getFontFromFile(ResourceLocation loc, float fontSize, int fontType) {
        try {
            Font output = Font.createFont(fontType, Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream());
            output = output.deriveFont(fontSize);
            return output;
        } catch (Exception var5) {
            return null;
        }
    }

    public static FontStorage getInstance() {
        return instance;
    }
}
