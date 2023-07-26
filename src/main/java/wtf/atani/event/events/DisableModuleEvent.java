package wtf.atani.event.events;

import wtf.atani.event.Event;
import wtf.atani.module.Module;

public class DisableModuleEvent extends Event {
    private final Module module;
    final DisableModuleEvent.Type type;

    public enum Type {
        PRE, POST;
    }

    public DisableModuleEvent(Module module, Type type) {
        this.module = module;
        this.type = type;
    }

    public Module getModule() {
        return module;
    }

    public Type getType() {
        return type;
    }
}
