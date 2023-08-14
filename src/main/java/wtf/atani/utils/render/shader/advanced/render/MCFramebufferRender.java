package wtf.atani.utils.render.shader.advanced.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.shader.Framebuffer;
import wtf.atani.utils.interfaces.Methods;

public class MCFramebufferRender implements Methods {

    public static void renderFramebuffer(final Framebuffer framebuffer) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
        framebuffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);
    }

}
