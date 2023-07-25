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
        this.add(this.createFontEntry("Roboto", 19),
                this.createFontEntry("Roboto", 16),
                this.createFontEntry("Roboto", 14));
    }

    public CustomFontRenderer findFont(String name, float size) {
        return this.getList().stream().filter(fontEntry -> fontEntry.getName().equalsIgnoreCase(name) && fontEntry.getSize() == size).findFirst().orElse(null).getFontRenderer();
    }

    private FontEntry createFontEntry(String name, float size) {
        return new FontEntry(name, size, new CustomFontRenderer(this.getFontFromTTF(new ResourceLocation(String.format("atani/%s.ttf", name)), size, Font.PLAIN), true));
    }

    private Font getFontFromTTF(ResourceLocation loc, float fontSize, int fontType) {
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
