package wtf.atani.utils.render.shader.advanced.access;

import wtf.atani.utils.render.shader.advanced.shaders.BloomShader;
import wtf.atani.utils.render.shader.advanced.shaders.BlurShader;

public interface ShaderAccess {

    BlurShader blurShader = new BlurShader();

    BloomShader bloomShader = new BloomShader();


}
