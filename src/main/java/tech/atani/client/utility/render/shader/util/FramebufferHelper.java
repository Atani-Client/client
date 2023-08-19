package tech.atani.client.utility.render.shader.util;

import net.minecraft.client.shader.Framebuffer;
import tech.atani.client.utility.interfaces.Methods;

public class FramebufferHelper implements Methods {

    public static Framebuffer doFrameBuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }


}