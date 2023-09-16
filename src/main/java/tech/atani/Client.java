package tech.atani;

import imgui.renderer.ImImpl;
import org.lwjglx.opengl.Display;
import tech.atani.command.Command;
import tech.atani.command.CommandManager;
import tech.atani.config.ConfigManager;
import tech.atani.event.bus.EventBus;
import tech.atani.module.Module;
import tech.atani.module.api.ModuleManager;
import tech.atani.theme.ThemeManager;
import tech.atani.utils.interfaces.IMethods;
import tech.atani.utils.misc.ReflectionUtil;

public enum Client implements IMethods {
    INSTANCE;

    public static final String NAME = "Atani";
    public static final String VERSION = "1.2";

    public static final boolean DEVELOPMENT_SWITCH = true;

    private EventBus eventBus;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private ConfigManager configManager;

    private ThemeManager themeManager;

    public void initClient() {
        mc.settings.guiScale = 2;
        mc.settings.ofFastRender = false;
        mc.settings.ofShowGlErrors = DEVELOPMENT_SWITCH;

        mc.settings.ofSmartAnimations = true;
        mc.settings.ofSmoothFps = false;
        mc.settings.ofFastMath = false;

        this.eventBus = new EventBus();
        this.moduleManager = new ModuleManager();
        this.commandManager = new CommandManager();
        this.configManager = new ConfigManager();

        this.themeManager = new ThemeManager();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            this.configManager.stop();
        }));

        String[] paths = {
                "tech.atani"
        };

        for (String path : paths) {
            if (!ReflectionUtil.exist(path))
                continue;

            Class<?>[] classes = ReflectionUtil.getClassesInPackage(path);

            for (Class<?> clazz : classes) {
                try {
                    if (Module.class.isAssignableFrom(clazz) && clazz != Module.class) {
                        this.moduleManager.add((Module) clazz.getConstructor().newInstance());
                    } else if (Command.class.isAssignableFrom(clazz) && clazz != Command.class) {
                        this.commandManager.add((Command) clazz.getConstructor().newInstance());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        this.moduleManager.init();
        this.commandManager.init();
        this.configManager.init();

        ImImpl.initialize(Display.getHandle());

        this.eventBus.register(this);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }
}
