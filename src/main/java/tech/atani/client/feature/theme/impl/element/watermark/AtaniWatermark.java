package tech.atani.client.feature.theme.impl.element.watermark;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.feature.module.impl.hud.WaterMark;
import tech.atani.client.feature.theme.data.ThemeObjectInfo;
import tech.atani.client.feature.theme.data.enums.ElementType;
import tech.atani.client.feature.theme.data.enums.ThemeObjectType;
import tech.atani.client.feature.theme.impl.element.DraggableElement;
import tech.atani.client.utility.math.atomic.AtomicFloat;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.utility.render.shader.render.ingame.RenderableShaders;

@ThemeObjectInfo(name = "Atani", themeObjectType = ThemeObjectType.ELEMENT, elementType = ElementType.WATERMARK)
public class AtaniWatermark extends DraggableElement {

    public AtaniWatermark() {
        super(10, 10, 0, 0, null, WaterMark.class);
    }

    @Override
    public void onDraw(ScaledResolution scaledResolution, float partialTicks, AtomicFloat leftY, AtomicFloat rightY) {
        RenderableShaders.renderAndRun(() -> {
            String text = CLIENT_NAME + " v" + CLIENT_VERSION + " | " + mc.getDebugFPS() + " fps";
            FontRenderer esp21 = FontStorage.getInstance().findFont("ESP", 21);
            float length = esp21.getStringWidthInt(text);
            float rectX = getPosX().getValue(), rectY = getPosY().getValue();
            float textX = rectX + 4, textY = rectY + 4.5f;
            float rectWidth = 8 + length, rectHeight = esp21.FONT_HEIGHT + 8;
            this.getWidth().setValue(rectWidth);
            this.getHeight().setValue(rectHeight);
            RenderUtil.drawRect(rectX, rectY, rectWidth, rectHeight, BACK_TRANS_180);
            esp21.drawStringWithShadow(text, textX, textY, -1);
            if(this.getLocked().getValue())
                leftY.set(rectY + rectHeight + 10);
        });
    }

}
