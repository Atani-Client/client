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

import java.awt.*;

@ThemeObjectInfo(name = "Koks", themeObjectType = ThemeObjectType.ELEMENT, elementType = ElementType.WATERMARK)
public class KoksWatermark extends ThemeObject {

    @Override
    public void onDraw(ScaledResolution scaledResolution, float partialTicks, AtomicFloat leftY, AtomicFloat rightY, Object[] params) {
        final String version = "v" + VERSION;

        FontRenderer roboto18 = FontStorage.getInstance().findFont("Roboto", 18);
        FontRenderer raleway30 = FontStorage.getInstance().findFont("Raleway Regular", 30);
        FontRenderer raleway35 = FontStorage.getInstance().findFont("Raleway Regular", 35);

        RenderUtil.startScissorBox();
        RenderUtil.drawScissorBox(8, 10, 100, 16f);
        raleway35.drawStringWithShadow(String.valueOf(CLIENT_NAME.charAt(0)), 10, 10, getRainbow(0, 3000, 0.6f, 1).getRGB());
        raleway30.drawStringWithShadow(CLIENT_NAME.substring(1) + "sense", 10 + raleway35.getStringWidth(String.valueOf(CLIENT_NAME.charAt(0))), 12, -1);
        RenderUtil.endScissorBox();

        for (int i = 0; i < version.length(); i++) {
            roboto18.drawStringWithShadow(String.valueOf(version.charAt(i)), 10 + raleway35.getStringWidth(String.valueOf(CLIENT_NAME.charAt(0))) + raleway30.getStringWidth(CLIENT_NAME.substring(1) + "sense") + roboto18.getStringWidth(version.substring(0, i)) - roboto18.getStringWidth(version), 8, getRainbow(100 * (i + 1), 3000, 0.6f, 1).getRGB());
        }

        final double motionX = getPlayer().posX - getPlayer().prevPosX;
        final double motionZ = getPlayer().posZ - getPlayer().prevPosZ;
        double speed = Math.sqrt(motionX * motionX + motionZ * motionZ) * 20 * getTimer().timerSpeed;
        speed = Math.round(speed * 10);
        speed = speed / 10;

        String bps = "Bps: ";


        for (int i = 0; i < bps.length(); i++) {
            final char character = bps.charAt(i);
            roboto18.drawStringWithShadow(character + "", 11 + roboto18.getStringWidth(bps.substring(0, i)), 28, getRainbow(100 * (i + 1), 3000, 0.6f, 1).getRGB());
        }

        roboto18.drawStringWithShadow(speed + "", 11 + roboto18.getStringWidth(bps), 28, new Color(-1).getRGB());

        leftY.set(50);
    }

    // Yea I skidded the method to get the exact same rainbow, SUE ME
    public Color getRainbow(int offset, int speed, float saturation, float brightness) {
        float hue = ((System.currentTimeMillis() + offset) % speed) / (float) speed;
        return Color.getHSBColor(hue, saturation, brightness);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
    
}
