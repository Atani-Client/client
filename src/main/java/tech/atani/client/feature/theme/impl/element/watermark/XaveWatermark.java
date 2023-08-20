package tech.atani.client.feature.theme.impl.element.watermark;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.feature.theme.ThemeObject;
import tech.atani.client.feature.theme.data.ThemeObjectInfo;
import tech.atani.client.feature.theme.data.enums.ElementType;
import tech.atani.client.feature.theme.data.enums.ThemeObjectType;
import tech.atani.client.utility.math.atomic.AtomicFloat;

import java.awt.*;

@ThemeObjectInfo(name = "Xave", themeObjectType = ThemeObjectType.ELEMENT, elementType = ElementType.WATERMARK)
public class XaveWatermark extends ThemeObject {

    @Override
    public void onDraw(ScaledResolution sr, float partialTicks, AtomicFloat leftY, AtomicFloat rightY) {
        FontRenderer fontRenderer = FontStorage.getInstance().findFont("ESP", 80);
        String text = CLIENT_NAME.toUpperCase() + "+";
        Gui.drawRect(sr.getScaledWidth() - fontRenderer.getStringWidth(text) - 1, fontRenderer.FONT_HEIGHT - 4, sr.getScaledWidth(), 0, new Color(0, 0, 0, 180).getRGB());
        fontRenderer.drawStringWithShadow(text, sr.getScaledWidth() - fontRenderer.getStringWidth(text) + 2, 4, -1);
        rightY.set(fontRenderer.FONT_HEIGHT - 4);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
    
}
