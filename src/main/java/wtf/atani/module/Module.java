package wtf.atani.module;

import wtf.atani.event.handling.EventHandling;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.interfaces.Methods;

public abstract class Module implements Methods {

    private final String name, description;
    private final Category category;
    private int key;
    private boolean enabled;

    public Module() {
        ModuleInfo moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
        if(moduleInfo == null)
            throw new RuntimeException();
        this.name = moduleInfo.name();
        this.description = moduleInfo.description();
        this.category = moduleInfo.category();
        this.key = moduleInfo.key();
    }

    public void toggle() {
        this.setEnabled(!this.isEnabled());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) {
            onModuleEnable();
        } else {
            onModuleDisable();
        }
    }

    private void onModuleEnable() {
        onEnable();
        EventHandling.getInstance().registerListener(this);
    }

    private void onModuleDisable() {
        EventHandling.getInstance().unregisterListener(this);
        onDisable();
    }

    public abstract void onEnable();
    public abstract void onDisable();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
