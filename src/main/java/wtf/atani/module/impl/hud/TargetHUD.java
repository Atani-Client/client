package wtf.atani.module.impl.hud;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import wtf.atani.event.events.Render2DEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.impl.combat.KillAura;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.combat.FightUtil;
import wtf.atani.utils.math.MathUtil;
import wtf.atani.utils.render.GradientUtil;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.color.ColorUtil;
import wtf.atani.value.impl.StringBoxValue;

import java.awt.*;

@SuppressWarnings("UnnecessaryUnicodeEscape")
@ModuleInfo(name = "TargetHUD", description = "Draws a little box with the targets info", category = Category.HUD)
public class TargetHUD extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Golden", "Astolfo"});

    @Listen
    public void onRender2D(Render2DEvent render2DEvent) {
        if(ModuleStorage.getInstance().getByClass(KillAura.class).isEnabled() && KillAura.curEntity != null && FightUtil.getRange(KillAura.curEntity) <= ModuleStorage.getInstance().getByClass(KillAura.class).attackRange.getValue().floatValue() && KillAura.curEntity instanceof EntityPlayer) {
            EntityLivingBase target = KillAura.curEntity;

            float x = render2DEvent.getScaledResolution().getScaledWidth() / 2 + 10;
            float y = render2DEvent.getScaledResolution().getScaledHeight() / 2 + 5;

            FontRenderer small = FontStorage.getInstance().findFont("Roboto", 17);
            FontRenderer mcRenderer = mc.fontRendererObj;

            int counter = 0;

            switch (this.mode.getValue()) {
                case "Golden":
                    float width = 100;
                    float height = 2 + 3 * (small.FONT_HEIGHT + 1);
                    RenderUtil.drawRect(x, y, width, height, new Color(20, 20, 20).getRGB());
                    small.drawStringWithShadow(target.getCommandSenderName(), x + 2 + height - 4 + 2 + 2, y + 2, -1);
                    small.drawStringWithShadow(MathUtil.round(target.getHealth(), 1) + " HP", x + 2 + height - 4 + 2 + 2, y + 2 + small.FONT_HEIGHT + 1, -1);
                    String predictedOutcome = "";
                    int roundedOwn = Math.round(mc.thePlayer.getHealth());
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
                    GradientUtil.drawGradientTB(x + width - 2, y, 2, height, 1, new Color(255, 202, 3), new Color(255, 84, 3));
                    RenderUtil.endScissorBox();
                    break;
                case "Astolfo":
                    RenderUtil.drawRect(x, y, 156, 57, new Color(0,0,0, 170).getRGB());

                    mcRenderer.drawString(target.getCommandSenderName(), x + 33, y + 6, Color.WHITE.getRGB());
                    mcRenderer.drawString(Math.round(target.getHealth() / 2.0f * 10) / 10 + " \u2764", x + 33, y + 20, new Color(ColorUtil.blendRainbowColours(counter * 150L)).getRGB());

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