package wtf.atani.utils.render.shader;

import net.minecraft.client.shader.Framebuffer;
import wtf.atani.utils.interfaces.Methods;

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