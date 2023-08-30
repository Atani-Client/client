package tech.atani.client.feature.module.impl.render;

import com.google.common.collect.Maps;
import com.sun.javafx.geom.Vec3d;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.listener.event.minecraft.render.Render2DEvent;
import tech.atani.client.listener.event.minecraft.render.Render3DEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.interpolation.InterpolationUtil;
import tech.atani.client.utility.player.combat.FightUtil;
import tech.atani.client.utility.render.RenderUtil;
import tech.atani.client.utility.render.color.ColorUtil;

import java.awt.*;
import java.util.Map;

@ModuleData(name = "Pointers", description = "Points at other entities", category = Category.RENDER)
public class Pointers extends Module {
    private int alpha;
    private SliderValue<Float> size = new SliderValue<>("Size", this, 10f, 5f, 25f, 1);
    private SliderValue<Float> width = new SliderValue<>("Width", this, 0.8f, 0.0f, 2.5f, 1);
    private SliderValue<Float> height = new SliderValue<>("Inside Height", this, 0.7f, 0f, 1f, 1);
    private SliderValue<Integer> radius = new SliderValue<>("Radius", this, 45, 10, 200, 0);
    private SliderValue<Float> outlineAlpha = new SliderValue<>("Outline Alpha", this, 0.6f, 0f, 1f, 1);
    private CheckBoxValue showOnPlayers = new CheckBoxValue("Render under Entities", this, true);
    public CheckBoxValue players = new CheckBoxValue("Players", "Attack Players?", this, true);
    public CheckBoxValue animals = new CheckBoxValue("Animals", "Attack Animals", this, true);
    public CheckBoxValue monsters = new CheckBoxValue("Monsters", "Attack Monsters", this, true);
    public CheckBoxValue invisible = new CheckBoxValue("Invisibles", "Attack Invisibles?", this, true);


    private Map<Entity, Vec3d> entityLowerBounds = Maps.newHashMap();

    @Override
    public void onEnable() {
        alpha = 0;
    }

    @Override
    public void onDisable() {

    }

    @Listen
    public void on3D(Render3DEvent render3DEvent) {
        if (!entityLowerBounds.isEmpty()) {
            entityLowerBounds.clear();
        }
        for (Entity e : mc.theWorld.loadedEntityList) {
            Vec3d bound = getEntityRenderPosition(e);
            bound.add(new Vec3d(0, e.height + 0.2, 0));
            Vec3d upperBounds = RenderUtil.to2D(bound.x, bound.y, bound.z), lowerBounds = RenderUtil.to2D(bound.x, bound.y - 2, bound.z);
            if (upperBounds != null && lowerBounds != null) {
                entityLowerBounds.put(e, lowerBounds);
            }
        }
    }

    private Vec3d getEntityRenderPosition(Entity entity) {
        double partial = mc.timer.renderPartialTicks;

        double x = InterpolationUtil.interpolate(entity.lastTickPosX, entity.posX, partial) - mc.getRenderManager().viewerPosX;
        double y = InterpolationUtil.interpolate(entity.lastTickPosY, entity.posY, partial) - mc.getRenderManager().viewerPosY;
        double z = InterpolationUtil.interpolate(entity.lastTickPosZ, entity.posZ, partial) - mc.getRenderManager().viewerPosZ;

        return new Vec3d(x, y, z);
    }

    @Listen
    public void onRender2D(Render2DEvent event) {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        for (final Entity entity : getWorld().loadedEntityList) {
            if (entity == getPlayer() && Methods.mc.gameSettings.thirdPersonView == 0) continue;
            if (!FightUtil.isValidWithPlayer(entity, radius.getValue(), invisible.getValue(), players.getValue(), animals.getValue(), monsters.getValue()))
                continue;
            Vec3d pos = entityLowerBounds.get(entity);

            if (pos != null) {
                if (isOnScreen(pos)) {
                    float x = (float) pos.x / scaledResolution.getScaleFactor();
                    float y = (float) pos.y / scaledResolution.getScaleFactor();
                    if (showOnPlayers.isEnabled()) {
                        drawPointer(x, y, size.getValue(), 3 - width.getValue(), 2 - height.getValue(), getColor((EntityLivingBase) entity, 255).getRGB());
                    }
                } else {
                    RenderUtil.pushMatrixAndAttrib();
                    int x = (Display.getWidth() / 2) / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale);
                    int y = (Display.getHeight() / 2) / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale);
                    float yaw = getRotations((EntityLivingBase) entity) - mc.thePlayer.rotationYaw;
                    GL11.glTranslatef(x, y, 0);
                    GL11.glRotatef(yaw, 0, 0, 1);
                    GL11.glTranslatef(-x, -y, 0);

                    drawPointer(x, y - radius.getValue(), size.getValue(), 3 - width.getValue(), 2 - height.getValue(), getColor((EntityLivingBase) entity, alpha).getRGB());

                    RenderUtil.resetColor();

                    RenderUtil.popMatrixAndAttrib();
                }
            }
        }
    }

    public void drawPointer(float x, float y, float size, float width, float heightIn, int color) {
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glPushMatrix();
        RenderUtil.color(color);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x - size / width, y + size);
        GL11.glVertex2d(x, y + size / heightIn);
        GL11.glVertex2d(x + size / width, y + size);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glColor4f(0, 0, 0, this.outlineAlpha.getValue());
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x - size / width, y + size);
        GL11.glVertex2d(x, y + size / heightIn);
        GL11.glVertex2d(x + size / width, y + size);
        GL11.glVertex2d(x, y);
        GL11.glEnd();
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        if (!blend)
            GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderUtil.resetColor();
    }

    private boolean isOnScreen(Vec3d pos) {
        if (pos.x > -1 && pos.z < 1)
            return pos.x / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) >= 0 && pos.x / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) <= Display.getWidth() && pos.y / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) >= 0 && pos.y / (mc.gameSettings.guiScale == 0 ? 1 : mc.gameSettings.guiScale) <= Display.getHeight();

        return false;
    }

    private float getRotations(EntityLivingBase ent) {
        final double x = ent.posX - mc.thePlayer.posX;
        final double z = ent.posZ - mc.thePlayer.posZ;
        final float yaw = (float) (-(Math.atan2(x, z) * 57.29577951308232));
        return yaw;
    }

    private Color getColor(EntityLivingBase player, int alpha) {
        float f = mc.thePlayer.getDistanceToEntity(player);
        float f1 = radius.getValue();
        float f2 = Math.max(f, f1);
        final Color clr = new Color(ColorUtil.blendHealthColours(f / 35));
        return new Color(clr.getRed(), clr.getGreen(), clr.getBlue(), alpha);
    }

}
