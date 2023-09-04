package tech.atani.client.feature.theme.impl.element.watermark;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.feature.theme.ThemeObject;
import tech.atani.client.feature.theme.data.ThemeObjectInfo;
import tech.atani.client.feature.theme.data.enums.ElementType;
import tech.atani.client.feature.theme.data.enums.ThemeObjectType;
import tech.atani.client.utility.math.atomic.AtomicFloat;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.utility.render.shader.shaders.GradientShader;

import java.awt.*;

@ThemeObjectInfo(name = "Fatality", themeObjectType = ThemeObjectType.ELEMENT, elementType = ElementType.WATERMARK)
public class FatalityWatermark extends ThemeObject {

    @Override
    public void onDraw(ScaledResolution scaledResolution, float partialTicks, AtomicFloat leftY, AtomicFloat rightY, Object[] params) {
        // TODO: implement usernames
        final String text = String.format("$$$ %s.vip $$$ | %s | %s", CLIENT_NAME.toLowerCase(), "idk", mc.isSingleplayer() ? "SinglePlayer" : mc.getCurrentServerData().serverIP);
        final float width2 = (float) (FontStorage.getInstance().findFont("Roboto", 15).getStringWidthInt(text) + 8);
        final int height2 = 20;
        final int posX2 = 2;
        final int posY1 = 2;
        Gui.drawRect(posX2, posY1, posX2 + width2 + 2.0f, posY1 + height2, new Color(5, 5, 5, 255).getRGB());
        RenderUtil.drawBorderedRect(posX2 + 0.5f, posY1 + 0.5f, posX2 + width2 + 1.5f, posY1 + height2 - 0.5f, 0.5f, new Color(40, 40, 40, 255).getRGB(), new Color(60, 60, 60, 255).getRGB(), true);
        RenderUtil.drawBorderedRect(posX2 + 2, posY1 + 2, posX2 + width2, posY1 + height2 - 2, 0.5f, new Color(22, 22, 22, 255).getRGB(), new Color(60, 60, 60, 255).getRGB(), true);
        Gui.drawRect(posX2 + 2.5, posY1 + 2.5, posX2 + width2 - 0.5, posY1 + 4.5, new Color(9, 9, 9, 255).getRGB());
        GradientShader.drawGradientLR(4.0f, posY1 + 3, width2 - 2, 1, 1, new Color(FATALITY_FIRST), new Color(FATALITY_SECOND));
        FontStorage.getInstance().findFont("Roboto", 15).drawStringWithShadow(text, 7.5F, 10.0f, Color.white.getRGB());
        leftY.set(24);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
    
}
