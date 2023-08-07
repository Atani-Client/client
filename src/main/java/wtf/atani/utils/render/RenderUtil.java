package wtf.atani.utils.render;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.src.Config;
import net.minecraft.util.AxisAlignedBB;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;
import wtf.atani.utils.interfaces.Methods;

import java.awt.*;

public class RenderUtil implements Methods {

    public static void scaleStart(float x, float y, float scale) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scale, scale, 1);
        GL11.glTranslatef(-x, -y, 0);
    }

    public static void scaleEnd() {
        GL11.glPopMatrix();
    }


    public static void drawBorderedRect(final float left, final float top, final float right, final float bottom, final float borderWidth, final int insideColor, final int borderColor, final boolean borderIncludedInBounds) {
        Gui.drawRect(left - (borderIncludedInBounds ? 0.0f : borderWidth), top - (borderIncludedInBounds ? 0.0f : borderWidth), right + (borderIncludedInBounds ? 0.0f : borderWidth), bottom + (borderIncludedInBounds ? 0.0f : borderWidth), borderColor);
        Gui.drawRect(left + (borderIncludedInBounds ? borderWidth : 0.0f), top + (borderIncludedInBounds ? borderWidth : 0.0f), right - (borderIncludedInBounds ? borderWidth : 0.0f), bottom - (borderIncludedInBounds ? borderWidth : 0.0f), insideColor);
    }
    public static void renderESP(Entity entity, boolean hurtTime, AxisAlignedBB boundingBox, boolean outline, boolean fill, Color color) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        if (entity == null || !hurtTime || ((EntityLivingBase)entity).hurtTime == 0) {
            GlStateManager.color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
        } else {
            GlStateManager.color(1.0F, 0.0F, 0.0F, color.getAlpha() / 255.0F);
        }
        GL11.glLineWidth(1.0F);
        GlStateManager.disableTexture2D();
        if (Config.isShaders())
            Shaders.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glDepthMask(false);
        GlStateManager.disableDepth();
        if (outline)
            drawOutline(entity, hurtTime, boundingBox, color);
        if (fill)
            drawFill(boundingBox);
        GlStateManager.enableDepth();
        GlStateManager.resetColor();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        if (Config.isShaders())
            Shaders.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    private static void drawOutline(Entity entity, boolean hurtTime, AxisAlignedBB boundingBox, Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        if (entity != null && hurtTime && ((EntityLivingBase)entity).hurtTime != 0) {
            r = 255;
            g = 0;
            b = 0;
        }
        RenderGlobal.func_181563_a(boundingBox, r, g, b, 255);
    }

    private static void drawFill(AxisAlignedBB boundingBox) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_NORMAL);
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawSkinHead(EntityLivingBase player, double x, double y, int size) {
        drawSkinHead(player, x, y, size, Color.WHITE);
    }

    public static void drawSkinHead(EntityLivingBase player, double x, double y, int size, Color color) {
        if (!(player instanceof EntityPlayer))
            return;

        try {
            GL11.glPushMatrix();
            mc.getTextureManager().bindTexture(((AbstractClientPlayer) player).getLocationSkin());
            GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);

            Gui.drawScaledCustomSizeModalRect((int) x, (int) y, 8, 8, 8, 8, size, size, 64, 64);
            GL11.glPopMatrix();
        } catch (Exception ignored) {
        }
    }

    public static void drawRect(float x, float y, float width, float height, int colour) {
        Gui.drawRect(x, y, x + width, y + height, colour);
    }

    public static void drawCheckMark(float x, float y, int width, int color) {
        float f = (color >> 24 & 255) / 255.0f;
        float f1 = (color >> 16 & 255) / 255.0f;
        float f2 = (color >> 8 & 255) / 255.0f;
        float f3 = (color & 255) / 255.0f;
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(2.2f);
        GL11.glBegin(3);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(x + width - 6.5, y + 3);
        GL11.glVertex2d(x + width - 11.5, y + 10);
        GL11.glVertex2d(x + width - 13.5, y + 8);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void startScissorBox() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
    }

    public static void drawScissorBox(double x, double y, double width, double height) {
        width = Math.max(width, 0.1);

        ScaledResolution sr = new ScaledResolution(mc);
        double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public static void drawScissorBox(double x, double y, double width, double height, double scale) {
        width = Math.max(width, 0.1);

        ScaledResolution sr = new ScaledResolution(mc);

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public static void endScissorBox() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight;
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, (float) (limit * .01));
    }

    public static void bindTexture(int texture) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
    }

    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void enableDepth() {
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
    }

    public static void disableDepth() {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
    }

    public static int[] enabledCaps = new int[32];

    public static void enableCaps(int... caps) {
        for (int cap : caps) GL11.glEnable(cap);
        enabledCaps = caps;
    }

    public static void disableCaps() {
        for (int cap : enabledCaps) GL11.glDisable(cap);
    }

    public static void startBlend() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public static void endBlend() {
        GlStateManager.disableBlend();
    }

    public static void setup2DRendering(boolean blend) {
        if (blend) {
            startBlend();
        }
        GlStateManager.disableTexture2D();
    }

    public static void setup2DRendering() {
        setup2DRendering(true);
    }

    public static void end2DRendering() {
        GlStateManager.enableTexture2D();
        endBlend();
    }

    public static void startRotate(float x, float y, float rotate) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.rotate(rotate, 0, 0, -1);
        GlStateManager.translate(-x, -y, 0);
    }

    public static void endRotate(){
        GlStateManager.popMatrix();
    }

    public static boolean isHovered(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }

    public static void drawAstolfoBorderedRect(float left, float top, float right, float bottom, float thickness, int color) {
        Gui.drawRect(left - thickness, top, left, bottom + 1.f, color);
        Gui.drawRect(right, top, right + thickness, bottom + 1.f, color);
        Gui.drawRect(left, top + thickness, right, top, color);
        Gui.drawRect(left, bottom, right, bottom + thickness, color);
    }

}
