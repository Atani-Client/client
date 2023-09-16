package tech.atani;

import imgui.renderer.ImImpl;
import org.lwjglx.opengl.Display;
import tech.atani.utils.interfaces.IMethods;

public enum Client implements IMethods {
    INSTANCE;

    public static final String NAME = "Atani";
    public static final String VERSION = "2.1";
    public static final boolean DEVELOPMENT_SWITCH = true;

    public void initClient() {
        mc.settings.guiScale = 2;
        mc.settings.ofFastRender = false;
        mc.settings.ofShowGlErrors = DEVELOPMENT_SWITCH;

        mc.settings.ofSmartAnimations = true;
        mc.settings.ofSmoothFps = false;
        mc.settings.ofFastMath = false;

        ImImpl.initialize(Display.getHandle());
    }
}
