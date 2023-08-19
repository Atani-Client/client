package tech.atani.client.feature.module.impl.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import de.florianmichael.rclasses.math.MathUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import tech.atani.client.listener.event.events.minecraft.render.Render2DEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.customFont.storage.FontStorage;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.module.impl.combat.KillAura;
import tech.atani.client.feature.module.storage.ModuleStorage;
import tech.atani.client.utility.player.combat.FightUtil;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.MathUtil;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.utility.render.color.ColorUtil;
import tech.atani.client.utility.render.shader.render.ingame.RenderableShaders;
import tech.atani.client.feature.module.value.impl.StringBoxValue;
import tech.atani.client.utility.interfaces.ColorPalette;
import tech.atani.client.utility.render.shader.shaders.GradientShader;

import java.awt.*;

@SuppressWarnings("UnnecessaryUnicodeEscape")
@ModuleData(name = "TargetHUD", description = "Draws a little box with the targets info", category = Category.HUD)
public class TargetHUD extends Module implements ColorPalette {

    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Simple", "Atani", "Golden", "Ryu", "Astolfo"});

    @Listen
    public void onRender2D(Render2DEvent render2DEvent) {
        if(ModuleStorage.getInstance().getByClass(KillAura.class).isEnabled() && KillAura.curEntity != null && FightUtil.getRange(KillAura.curEntity) <= ModuleStorage.getInstance().getByClass(KillAura.class).attackRange.getValue().floatValue() && KillAura.curEntity instanceof EntityPlayer) {
            EntityLivingBase target = KillAura.curEntity;

            float x = render2DEvent.getScaledResolution().getScaledWidth() / 2 + 10;
            float y = render2DEvent.getScaledResolution().getScaledHeight() / 2 + 5;

            FontRenderer small = FontStorage.getInstance().findFont("Roboto", 17);
            FontRenderer mcRenderer = Methods.mc.fontRendererObj;

            int counter = 0;

            switch (this.mode.getValue()) {
                case "Ryu": {
                    float height = 35;
                    RenderUtil.drawRect(x, y, height, height, new Color(0, 0, 0, 140).getRGB());
                    RenderUtil.drawSkinHead(target, x + 4, y + 4, (int)height - 8);
                    RenderUtil.drawRect(x + height, y, 3, height, ColorUtil.setAlpha(new Color(RYU), 140).getRGB());
                    RenderUtil.drawRect(x + height + 3, y, 70, height, new Color(0, 0, 0, 80).getRGB());
                    RenderableShaders.render(true, false, () -> {
                        FontRenderer medium21 = FontStorage.getInstance().findFont("Roboto", 21);
                        FontRenderer medium17 = FontStorage.getInstance().findFont("Roboto", 17);
                        float health = MathUtils.clamp(target.getHealth(), 0, 20);
                        medium21.drawString(target.getCommandSenderName(), x + height + 3 + 4 + 0.5f, y + 5 + 0.5f, Color.black.getRGB());
                        medium17.drawString("Health " + health, x + height + 3 + 4 + 0.5f, y + 5 + medium21.FONT_HEIGHT + 2 + 0.5f, Color.black.getRGB());
                        medium17.drawString("Distance " + MathUtil.round(FightUtil.getRange(target), 1), x + height + 3 + 4 + 0.5f, y + 5 + medium21.FONT_HEIGHT + 2 + medium17.FONT_HEIGHT + 0.5f, Color.black.getRGB());
                    });
                    FontRenderer medium21 = FontStorage.getInstance().findFont("Roboto Medium", 21);
                    FontRenderer medium17 = FontStorage.getInstance().findFont("Roboto Medium", 17);
                    float health = MathUtils.clamp(target.getHealth(), 0, 20);
                    medium21.drawString(target.getCommandSenderName(), x + height + 3 + 4, y + 5, -1);
                    medium17.drawString("Health " + ChatFormatting.WHITE + health, x + height + 3 + 4, y + 5 + medium21.FONT_HEIGHT + 2, RYU);
                    medium17.drawString("Distance " + ChatFormatting.WHITE + MathUtil.round(FightUtil.getRange(target), 1), x + height + 3 + 4, y + 5 + medium21.FONT_HEIGHT + 2 + medium17.FONT_HEIGHT, RYU);
                    break;
                }
                case "Atani": {
                    float width = 120, height = 50;
                    RenderUtil.drawRect(x, y, width, height, new Color(0, 58, 105).brighter().getRGB());
                    GradientShader.drawGradientTB(x + 2, y + 2, width - 4, 14, 1, new Color(0, 48, 95).brighter().brighter(), new Color(0, 48, 95).brighter());
                    RenderUtil.drawRect(x + 2, y + 14, width - 4, height - 14 - 2, new Color(240, 240, 240).getRGB());
                    FontRenderer fontRenderer = FontStorage.getInstance().findFont("Arial", 19);
                    fontRenderer.drawStringWithShadow(target.getCommandSenderName(), x + 3.5f, y + 3.5f, -1);
                    int headSize = (int) (height - 14 - 6);
                    RenderUtil.drawSkinHead(target, x + 2 + 2, y + 14 + 2, headSize);
                    RenderUtil.drawBorderedRect(x + 6 + headSize, y + 14 + 2, (x + 6 + headSize) + width - 10 - headSize, (y + 14 + 2) + headSize, 1f,-1, new Color(200, 200, 200).getRGB(), true);
                    float health = MathUtils.clamp(target.getHealth(), 0, 20);
                    float percentage = health / 20;
                    RenderUtil.drawRect(x + 6 + headSize + 2, y + 14 + 2 + 2, width - (6 + headSize + 8), 6, new Color(240, 240, 240).getRGB());
                    GradientShader.drawGradientTB(x + 6 + headSize + 2, y + 14 + 2 + 2, (width - (6 + headSize + 8)) * percentage, 6, 1, Color.red, Color.red.darker().darker());
                    FontRenderer smallArial = FontStorage.getInstance().findFont("Arial", 17);
                    Color textColor = new Color(15, 15, 15);
                    fontRenderer.drawString( ChatFormatting.BOLD.toString() + ((int)health) + " HP (" + ((int)(percentage * 100)) + "%)", x + 6 + headSize + 3, y + 14 + 2 + 2 + 9, textColor.getRGB());
                    int roundedOwn = Math.round(Methods.mc.thePlayer.getHealth());
                    int roundedTarget = Math.round(target.getHealth());
                    int status = 0;
                    if(roundedOwn == roundedTarget) {
                        status = 0;
                    } else if(roundedOwn < roundedTarget) {
                        status = 1;
                    } else if(roundedOwn > roundedTarget) {
                        status = 2;
                    }
                    fontRenderer.drawString(status == 0 ? "Draw" : status == 1 ? "Lose" : "Win", x + 6 + headSize + 3, y + 14 + 2 + 2 + 10 + smallArial.FONT_HEIGHT, status == 0 ? Color.yellow.darker().getRGB() : status == 1 ? Color.red.darker().getRGB() : Color.green.darker().getRGB());
                    break;
                }
                case "Simple":
                    RenderableShaders.renderAndRun(() -> {
                        String text = target.getCommandSenderName() + " | " + Math.round(Methods.mc.thePlayer.getHealth());
                        FontRenderer roboto17 = FontStorage.getInstance().findFont("Roboto", 17);
                        float length = roboto17.getStringWidth(text);
                        float textX = x + 4, textY = y + 4.5f;
                        float rectWidth = 8 + length, rectHeight = roboto17.FONT_HEIGHT + 8;
                        RenderUtil.drawRect(x, y, rectWidth, rectHeight, BACK_TRANS_180);
                        roboto17.drawStringWithShadow(text, textX, textY, -1);
                    });
                    break;
                case "Golden":
                    float width = 100;
                    float height = 2 + 3 * (small.FONT_HEIGHT + 1);
                    RenderUtil.drawRect(x, y, width, height, new Color(20, 20, 20).getRGB());
                    small.drawStringWithShadow(target.getCommandSenderName(), x + 2 + height - 4 + 2 + 2, y + 2, -1);
                    small.drawStringWithShadow(MathUtil.round(target.getHealth(), 1) + " HP", x + 2 + height - 4 + 2 + 2, y + 2 + small.FONT_HEIGHT + 1, -1);
                    String predictedOutcome = "";
                    int roundedOwn = Math.round(Methods.mc.thePlayer.getHealth());
                    int roundedTarget = Math.round(target.getHealth());
                    if(roundedOwn == roundedTarget) {
                        predictedOutcome = ChatFormatting.YELLOW + "Draw";
                    } else if(roundedOwn < roundedTarget) {
                        predictedOutcome = ChatFormatting.RED + "Losing";
                    } else if(roundedOwn > roundedTarget) {
                        predictedOutcome = ChatFormatting.GREEN + "Winning";
                    }
                    small.drawStringWithShadow(predictedOutcome, x + 2 + height - 4 + 2 + 2, y + 2 + small.FONT_HEIGHT + 1 + small.FONT_HEIGHT + 1, -1);
                    RenderUtil.drawSkinHead(target, x + 2, y + 2, (int) height - 4);
                    float healthPoint = height / 20;
                    RenderUtil.startScissorBox();
                    RenderUtil.drawScissorBox(x + width - 2, y + (20 * healthPoint) - (target.getHealth() * healthPoint), 2, target.getHealth() * healthPoint);
                    GradientShader.drawGradientTB(x + width - 2, y, 2, height, 1, new Color(255, 202, 3), new Color(255, 84, 3));
                    RenderUtil.endScissorBox();
                    break;
                case "Astolfo":
                    RenderUtil.drawRect(x, y, 156, 57, new Color(0,0,0, 170).getRGB());

                    mcRenderer.drawString(target.getCommandSenderName(), x + 33, y + 6, Color.WHITE.getRGB());

                    RenderUtil.scaleStart(x + 33, y + 20, 2.5f);
                    mcRenderer.drawStringWithShadow(Math.round(target.getHealth() / 2.0f * 10) / 10 + " \u2764", x + 33, y + 20, new Color(ColorUtil.blendRainbowColours(counter * 150L)).getRGB());
                    RenderUtil.scaleEnd();

                    GlStateManager.color(1.0f,1.0f,1.0f,1.0f);
                    GuiInventory.drawEntityOnScreen((int) (x + 17), (int) (y + 54), 25, target.rotationYaw, -target.rotationPitch, target);
                    RenderUtil.drawRect(x + 30, y + 46, 120, 8, new Color(ColorUtil.blendRainbowColours(counter * 150L)).darker().darker().darker().getRGB());
                    RenderUtil.drawRect(x + 30, y + 46, target.getHealth() / target.getMaxHealth() * 120, 8, new Color(ColorUtil.blendRainbowColours(counter * 150L)).getRGB());
                    RenderUtil.drawRect(x + 30 + target.getHealth() / target.getMaxHealth() * 120 - 3, y + 46, 3, 8, new Color (-1979711488, true).getRGB());
                    counter++;
                    break;
            }
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}