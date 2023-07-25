package wtf.atani.utils.render.shader.render.ingame;

import java.util.Arrays;

import net.minecraft.client.shader.Framebuffer;
import wtf.atani.utils.render.shader.access.ShaderAccess;
import wtf.atani.utils.render.shader.data.ShaderRenderType;
import wtf.atani.utils.render.shader.render.Type;

public interface RenderableShaders {

    static void render(boolean bloom, boolean blur, Runnable... runnables) {
    	if(bloom) {
			ShaderAccess.bloomShader.doRender(ShaderRenderType.OVERLAY, Type.QUADS, Arrays.asList(runnables));
    	}	
    	if(blur) {
        	ShaderAccess.blurShader.doRender(ShaderRenderType.OVERLAY, Type.QUADS, Arrays.asList(runnables));
    	}
    }
    
    static void render(Runnable... runnables) {
    	RenderableShaders.render(true, true, runnables);
    }
    
    static void renderAndRun(boolean bloom, boolean blur, Runnable... runnables) {
    	RenderableShaders.render(bloom, blur, runnables);
        Arrays.asList(runnables).forEach(run -> run.run());
    }
    
    static void renderAndRun(Runnable... runnables) {
    	RenderableShaders.render(true, true, runnables);
        Arrays.asList(runnables).forEach(run -> run.run());
    }

}
