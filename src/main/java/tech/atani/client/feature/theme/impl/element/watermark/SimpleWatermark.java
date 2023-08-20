package tech.atani.client.feature.theme.impl.element.watermark;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.feature.theme.ThemeObject;
import tech.atani.client.feature.theme.data.ThemeObjectInfo;
import tech.atani.client.feature.theme.data.enums.ElementType;
import tech.atani.client.feature.theme.data.enums.ThemeObjectType;
import tech.atani.client.utility.math.atomic.AtomicFloat;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.utility.render.shader.render.ingame.RenderableShaders;

@ThemeObjectInfo(name = "Simple", themeObjectType = ThemeObjectType.ELEMENT, elementType = ElementType.WATERMARK)
public class SimpleWatermark extends ThemeObject {

    @Override
    public void onDraw(ScaledResolution scaledResolution, float partialTicks, AtomicFloat leftY, AtomicFloat rightY) {
        RenderableShaders.renderAndRun(() -> {
            String text = CLIENT_NAME + " v" + VERSION + " | " + mc.getDebugFPS() + " fps";
            FontRenderer roboto17 = FontStorage.getInstance().findFont("Roboto", 17);
            float length = roboto17.getStringWidth(text);
            float rectX = 10, rectY = 10;
            float textX = rectX + 4, textY = rectY + 4.5f;
            float rectWidth = 8 + length, rectHeight = roboto17.FONT_HEIGHT + 8;
            RenderUtil.drawRect(rectX, rectY, rectWidth, rectHeight, BACK_TRANS_180);
            roboto17.drawStringWithShadow(text, textX, textY, -1);
            leftY.set(rectY + rectHeight + 10);
        });
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
    
}
