package tech.atani.client.utility.render.shader.render;

import cn.muyang.nativeobfuscator.Native;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.shader.Framebuffer;
import tech.atani.client.utility.interfaces.Methods;
@Native
public class MCFramebufferRender implements Methods {

    public static void renderFramebuffer(final Framebuffer framebuffer) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, framebuffer.framebufferTexture);
        framebuffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);
    }

}
