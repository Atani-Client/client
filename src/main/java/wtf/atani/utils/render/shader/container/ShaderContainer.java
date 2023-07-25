package wtf.atani.utils.render.shader.container;

import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.render.shader.annotation.Info;
import wtf.atani.utils.render.shader.data.ShaderRenderType;
import wtf.atani.utils.render.shader.render.Type;

import java.util.List;

public abstract class ShaderContainer extends ShaderReload implements Methods {

    public String vert, frag;

    public ShaderContainer() {
        final Info info = getClass().getAnnotation(Info.class);
        vert = info.vert();
        frag = info.frag();
    }


    protected abstract void reload();

    public abstract void doRender(final ShaderRenderType type, Type renderType, List<Runnable> runnables);

}
