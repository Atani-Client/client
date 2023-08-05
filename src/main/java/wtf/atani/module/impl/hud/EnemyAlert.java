package wtf.atani.module.impl.hud;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import wtf.atani.event.events.Render2DEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.player.RotationUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;

import java.awt.*;
@ModuleInfo(name = "EnemyAlert", description = "Alerts about close enemies", category = Category.HUD)
public class EnemyAlert extends Module {

    private CheckBoxValue showDistance = new CheckBoxValue("Show Distance", "Show distance of enemies", this, true);
    private SliderValue<Float> maxDistance = new SliderValue<>("Max Distance", "What'll be the maximum distance to alert?", this, 20.0f, 10.0f, 50.0f, 1);

    @Listen
    public void on2D(Render2DEvent render2DEvent) {
        EntityPlayer entityPlayer = null;
        float floatValue = this.maxDistance.getValue();
        for (final EntityPlayer entityPlayer2 : mc.theWorld.playerEntities) {
            if (entityPlayer2 != null && entityPlayer2 != mc.thePlayer) {
                final float distanceToEntity = mc.thePlayer.getDistanceToEntity((Entity)entityPlayer2);
                if (distanceToEntity > floatValue) {
                    continue;
                }
                floatValue = distanceToEntity;
                entityPlayer = entityPlayer2;
            }
        }
        if (entityPlayer != null) {
            this.renderAlert(entityPlayer, render2DEvent.getScaledResolution());
        }
    }

    private void renderAlert(final EntityPlayer entityPlayer, final ScaledResolution scaledResolution) {
        final float wrapAngleTo180_float = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - RotationUtil.getRotation(entityPlayer, false, false, 0, 0, 0, 0, false, 0, 0, 0, 0, true, true)[0]);
        if (Math.abs(wrapAngleTo180_float) >= 90.0f) {
            final double width = scaledResolution.getScaledWidth() / 2.0f;
            final double height = scaledResolution.getScaledHeight() / 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translate(width, height, 1.0);
            GlStateManager.rotate(-(wrapAngleTo180_float + 100.0f), 0.0f, 0.0f, 1.0f);
            GlStateManager.translate(-width, -height, 1.0);
            FontStorage.getInstance().findFont("Roboto", 21).drawString(">", (int)width + 10, (int)height - 2, Color.RED.getRGB());
            GlStateManager.popMatrix();
            if (!(boolean)this.showDistance.getValue()) {
                return;
            }
            final String concat = Integer.toString(Math.round(mc.thePlayer.getDistanceToEntity(entityPlayer))).concat("m");
            FontStorage.getInstance().findFont("Roboto", 21).drawStringWithShadow(concat, (scaledResolution.getScaledWidth() - FontStorage.getInstance().findFont("Roboto", 21).getStringWidth(concat)) / 2.0f, (scaledResolution.getScaledHeight() - FontStorage.getInstance().findFont("Roboto", 21).FONT_HEIGHT * 3) / 2.0f, Color.RED.getRGB());
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
