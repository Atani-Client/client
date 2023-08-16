package wtf.atani.utils.render.shader.access;

import wtf.atani.utils.render.shader.shaders.BloomShader;
import wtf.atani.utils.render.shader.shaders.BlurShader;

public interface ShaderAccess {

    BlurShader blurShader = new BlurShader();

    BloomShader bloomShader = new BloomShader();


}
