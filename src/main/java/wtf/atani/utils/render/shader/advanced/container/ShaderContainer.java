package wtf.atani.utils.render.shader.advanced.container;

import wtf.atani.utils.interfaces.Methods;
import wtf.atani.utils.render.shader.advanced.annotation.Info;
import wtf.atani.utils.render.shader.advanced.data.ShaderRenderType;
import wtf.atani.utils.render.shader.advanced.render.Type;

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
