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
        this.add(this.createFontEntry("Roboto", 21),
                this.createFontEntry("Roboto", 19),
                this.createFontEntry("Roboto", 17),
                this.createFontEntry("Roboto", 16),
                this.createFontEntry("Roboto", 14),
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
