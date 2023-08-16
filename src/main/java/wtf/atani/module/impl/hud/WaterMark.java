package wtf.atani.module.impl.hud;

import com.google.common.base.Supplier;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import wtf.atani.event.events.Render2DEvent;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.impl.hud.clientOverlay.IClientOverlayComponent;
import wtf.atani.utils.interfaces.ColorPalette;
import wtf.atani.utils.java.StringUtil;
import wtf.atani.utils.math.atomic.AtomicFloat;
import wtf.atani.utils.render.color.ColorUtil;
import wtf.atani.utils.render.shader.legacy.shaders.GradientShader;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.shader.advanced.render.ingame.RenderableShaders;
import wtf.atani.utils.render.shader.legacy.shaders.RoundedShader;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

@ModuleInfo(name = "WaterMark", description = "A nice little overlay that shows you info about the client", category = Category.HUD)
public class WaterMark extends Module implements ColorPalette, IClientOverlayComponent {

    private StringBoxValue watermarkMode = new StringBoxValue("Watermark Mode", "Which watermark will be displayed?", this, new String[]{"None", "Atani Modern", "Simple", "Golden", "Augustus 2.6", "Xave", "Ryu", "Icarus", "Fatality"});
    private SliderValue<Integer> red = new SliderValue<>("Red", "What'll be the red of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> watermarkMode.getValue().equalsIgnoreCase("Atani Modern")});
    private SliderValue<Integer> green = new SliderValue<>("Green", "What'll be the green of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> watermarkMode.getValue().equalsIgnoreCase("Atani Modern")});
    private SliderValue<Integer> blue = new SliderValue<>("Blue", "What'll be the blue of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> watermarkMode.getValue().equalsIgnoreCase("Atani Modern")});
    @Override
    public void draw(Render2DEvent render2DEvent, AtomicFloat leftY, AtomicFloat rightY) {
        if(this.isEnabled()) {
            ScaledResolution sr = render2DEvent.getScaledResolution();

            switch (watermarkMode.getValue()) {
                case "Atani Modern": {
                    String text =  CLIENT_NAME + " | " + mc.getSession().getUsername() + " | " + mc.getDebugFPS() + " FPS";
                    FontRenderer fontRenderer = FontStorage.getInstance().findFont("Greycliff Medium", 21);
                    float outlineWidth = 1;
                    Color firstColor = new Color(red.getValue(), green.getValue(), blue.getValue());
                    int color = ColorUtil.fadeBetween(firstColor.getRGB(), firstColor.brighter().getRGB(), 1 * 150L);
                    RenderableShaders.renderAndRun(() -> {
                        RoundedShader.drawRoundOutline(10 - outlineWidth, 10 - outlineWidth, fontRenderer.getStringWidth(text) + 8 + outlineWidth * 2,fontRenderer.FONT_HEIGHT + 4 + outlineWidth * 2, 5, outlineWidth, new Color(20, 20, 20), new Color(color));
                    });
                    fontRenderer.drawString(text, 14f, 11.5f, -1);
                    leftY.set(20 + fontRenderer.FONT_HEIGHT + 4 + outlineWidth * 2);
                    break;
                }
                case "Xave": {
                    FontRenderer fontRenderer = FontStorage.getInstance().findFont("ESP", 80);
                    String text = CLIENT_NAME.toUpperCase() + "+";
                    Gui.drawRect(sr.getScaledWidth() - fontRenderer.getStringWidth(text) - 1, fontRenderer.FONT_HEIGHT - 4, sr.getScaledWidth(), 0, new Color(0, 0, 0, 180).getRGB());
                    fontRenderer.drawStringWithShadow(text, sr.getScaledWidth() - fontRenderer.getStringWidth(text) + 2, 4, -1);
                    rightY.set(fontRenderer.FONT_HEIGHT - 4);
                    break;
                }
                case "Fatality": {
                    // TODO: implement usernames
                    // Pasted this from some random radium paste since this client's not expensive enough for me to do random themes of dead, nn clients like this and do shit like remake the entire style of skeet fucking watermark for it
                    final String text = String.format("$$$ %s.vip $$$ | %s | %s", CLIENT_NAME.toLowerCase(), "idk", mc.isSingleplayer() ? "SinglePlayer" : mc.getCurrentServerData().serverIP);
                    final float width2 = (float) (FontStorage.getInstance().findFont("Roboto", 15).getStringWidth(text) + 8);
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
                    break;
                }
                case "Icarus": {
                    FontRenderer fontRenderer = FontStorage.getInstance().findFont("Pangram Bold", 80);
                    fontRenderer.drawStringWithShadow(CLIENT_NAME, 8, 0, -1);
                    leftY.set(fontRenderer.FONT_HEIGHT + 8);
                    break;
                }
                case "Ryu": {
                    FontRenderer fontRenderer = FontStorage.getInstance().findFont("Roboto Medium", 17);
                    String text = CLIENT_NAME + " " + ChatFormatting.GRAY + " # " + ChatFormatting.WHITE + " " + mc.getDebugFPS() + "fps";
                    // Rendering bloom on text is really performance fucking due to all the geometry around letter so we're trying to not hurt it as much
                    // by disabling the blur shader
                    RenderableShaders.renderAndRun(true, false, () -> {
                        // Rendering with the regular version of the font to fix a weird bug with the medium font where it looks shitty
                        FontStorage.getInstance().findFont("Roboto", 17).drawString(StringUtil.removeFormattingCodes(text), 3, 3, new Color(0, 0, 0).getRGB());
                    });
                    fontRenderer.drawString(text, 3, 3, RYU);
                    leftY.set(fontRenderer.FONT_HEIGHT + 6);
                    break;
                }
                case "Augustus 2.6": {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Date now = new Date();
                    String time = dateFormat.format(now);
                    String text = String.format("%s b%s" + ChatFormatting.GRAY + " (%s)", CLIENT_NAME, VERSION, time);
                    RenderUtil.drawRect(0, 0, 2 + mc.fontRendererObj.getStringWidth(text), 2 + mc.fontRendererObj.FONT_HEIGHT, new Color(0, 0, 0, 100).getRGB());
                    mc.fontRendererObj.drawStringWithShadow(text, 1, 2, -1);
                    leftY.set(2 + mc.fontRendererObj.FONT_HEIGHT);
                    break;
                }
                case "Simple":
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
                    break;
                case "Golden":
                    RenderableShaders.renderAndRun(() -> {
                        String text = CLIENT_NAME + " v" + VERSION + " | " + mc.getDebugFPS() + " fps";
                        FontRenderer roboto17 = FontStorage.getInstance().findFont("Roboto", 17);
                        float length = roboto17.getStringWidth(text);
                        float x = 5 + 2, y = 5, lineHeight = 2;
                        GradientShader.drawGradientLR(x, y, length + 5, lineHeight, 1, new Color(GOLDEN_FIRST), new Color(GOLDEN_SECOND));
                        RenderUtil.drawRect(x - 2, y, 2, roboto17.FONT_HEIGHT + 4, new Color(255, 202, 3).getRGB());
                        RenderUtil.drawRect(x, y + lineHeight, length + 5, roboto17.FONT_HEIGHT + 4 - lineHeight, BACK_GRAY_20);
                        roboto17.drawStringWithShadow(text, x + 2.5f, y + lineHeight + 2, -1);
                        leftY.set(y + lineHeight + roboto17.FONT_HEIGHT + 4 + y);
                    });
                    break;
            }
        }
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
