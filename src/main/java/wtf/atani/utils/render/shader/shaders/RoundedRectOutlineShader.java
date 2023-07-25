package wtf.atani.utils.render.shader.shaders;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.render.shader.ShaderProgram;
import wtf.atani.utils.render.shader.ShaderType;
import wtf.atani.utils.render.shader.TextureRenderer;

public class RoundedRectOutlineShader implements Methods {

    public ShaderProgram shaderProgram = new ShaderProgram("vertex/vertex.vsh", "/fragment/roundedoutline.glsl", ShaderType.GLSL);

    public void drawRound(float x, float y, float width, float height, float radius, float outlineThickness, Color color) {
        GlStateManager.resetColor();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        shaderProgram.initShader();
        setupRoundedRectUniforms(x, y, width, height, radius, outlineThickness);
        shaderProgram.setUniformf("outlineColor", color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
        TextureRenderer.drawTexture(x - 1, y - 1, width + 2, height + 2);
        shaderProgram.deleteShader();
        GlStateManager.disableBlend();
    }

    private void setupRoundedRectUniforms(float x, float y, float width, float height, float radius, float outlineThickness) {
        final ScaledResolution sr = new ScaledResolution(mc);
        shaderProgram.setUniformf("location", x * sr.getScaleFactor(), (mc.displayHeight - (height * sr.getScaleFactor())) - (y * sr.getScaleFactor()));
        shaderProgram.setUniformf("rectSize", width * sr.getScaleFactor(), height * sr.getScaleFactor());
        shaderProgram.setUniformf("radius", radius * sr.getScaleFactor());
        shaderProgram.setUniformf("outlineThickness", outlineThickness);
    }

}