package wtf.atani.utils.render.shader.shaders;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;

import net.minecraft.client.shader.Framebuffer;
import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.render.shader.FramebufferHelper;
import wtf.atani.utils.render.shader.ShaderProgram;
import wtf.atani.utils.render.shader.ShaderType;
import wtf.atani.utils.render.shader.TextureRenderer;

public class AcrylBlurShader implements Methods {

    public ShaderProgram shaderProgram = new ShaderProgram("vertex/vertex.vsh", "/fragment/acrylblur.glsl", ShaderType.GLSL);

    public static Framebuffer framebuffer = new Framebuffer(1, 1, false);

    public void draw() {
        framebuffer = FramebufferHelper.doFrameBuffer(framebuffer);

        shaderProgram.initShader();

        //first pass
        setupUniforms(1, 0);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, mc.getFramebuffer().framebufferTexture);
        TextureRenderer.drawTexture(framebuffer);
        framebuffer.unbindFramebuffer();

        //second pass
        shaderProgram.initShader();
        setupUniforms(0, 1);
        mc.getFramebuffer().bindFramebuffer(true);
        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        TextureRenderer.drawTexture(framebuffer);
        shaderProgram.deleteShader();
    }

    private void setupUniforms(int x, int y) {
        shaderProgram.setUniformi("currentTexture", 0);
        shaderProgram.setUniformf("texelSize", (float) (1.0 / mc.displayWidth), (float) (1.0 / mc.displayHeight));
        shaderProgram.setUniformf("coords", x, y);
        shaderProgram.setUniformf("blurRadius", 20);
        shaderProgram.setUniformf("blursigma", 12);
    }

}