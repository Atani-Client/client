package tech.atani.client.feature.theme.impl.element.modulelist;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import tech.atani.client.feature.font.storage.FontStorage;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.theme.data.ThemeObjectInfo;
import tech.atani.client.feature.theme.data.enums.ElementType;
import tech.atani.client.feature.theme.data.enums.ThemeObjectType;
import tech.atani.client.utility.math.atomic.AtomicFloat;
import tech.atani.client.utility.render.animation.Direction;
import tech.atani.client.utility.render.animation.impl.DecelerateAnimation;
import tech.atani.client.utility.render.color.ColorUtil;
import tech.atani.client.utility.render.shader.shaders.GradientShader;
import tech.atani.client.utility.render.shader.shaders.RoundedShader;

import java.awt.*;
import java.util.LinkedHashMap;

@ThemeObjectInfo(name = "Fatality", themeObjectType = ThemeObjectType.ELEMENT, elementType = ElementType.MODULE_LIST)
public class FatalityModuleList extends ModuleListElement {

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onDraw(ScaledResolution scaledResolution, float partialTicks, AtomicFloat leftY, AtomicFloat rightY, LinkedHashMap<Module, DecelerateAnimation> moduleHashMap) {
        if (rightY.get() == 0)
            rightY.set(1);
        FontRenderer fontRenderer = mc.fontRendererObj;
        float moduleY = rightY.get();
        int counter = 0;
        for (Module module : moduleHashMap.keySet()) {
            if (!moduleHashMap.get(module).finished(Direction.BACKWARDS)) {
                float moduleHeight = fontRenderer.FONT_HEIGHT;
                float rectLength = (float) ((fontRenderer.getStringWidth(module.getName()) + 1) * moduleHashMap.get(module).getOutput());
                fontRenderer.drawStringWithShadow(module.getName(), scaledResolution.getScaledWidth() - rectLength - 1, moduleY + moduleHeight / 2 - fontRenderer.FONT_HEIGHT / 2, ColorUtil.fadeBetween(FATALITY_FIRST, FATALITY_SECOND, counter * 150L));
                moduleY += moduleHeight;
                counter++;
            }
        }
    }

    @Override
    public boolean shouldAnimate() {
        return true;
    }

    @Override
    public FontRenderer getFontRenderer() {
        return mc.fontRendererObj;
    }

}
