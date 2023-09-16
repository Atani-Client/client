package tech.atani.module.api;

import tech.atani.Client;
import tech.atani.event.IEventListener;
import tech.atani.event.Priorities;
import tech.atani.event.annotations.SubscribeEvent;
import tech.atani.event.impl.input.KeyboardInputEvent;
import tech.atani.module.Module;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ModuleManager extends ArrayList<Module> {

    public void init() {
        this.stream().filter(m -> m.autoEnabled).forEach(m -> m.setToggled(true));

        Client.INSTANCE.getEventBus().register(this);
    }

    public <T extends Module> T get(final String name) {
        // noinspection unchecked
        return (T) this.stream()
                .filter(module -> module.getName().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }

    public <T extends Module> T get(final Class<T> clazz) {
        // noinspection unchecked
        return (T) this.stream()
                .filter(module -> module.getClass() == clazz)
                .findAny().orElse(null);
    }

    public List<Module> get(final Module.Category category) {
        return this.stream()
                .filter(module -> module.getInfo().category() == category)
                .collect(Collectors.toList());
    }


    @SubscribeEvent(value = Priorities.VERY_HIGH)
    private final IEventListener<KeyboardInputEvent> onKey = e -> {
        if (e.getGuiScreen() != null)
            return;

        this.stream().filter(m -> m.getKeyBind() == e.getKeyCode()).forEach(Module::toggle);
    };

    public ArrayList<Module> getModulesFromCategory(Module.Category category) {
        ArrayList<Module> modulesInCategory = new ArrayList<>();

        for (Module m : this)
            if (m.getInfo().category() == category)
                modulesInCategory.add(m);

        return modulesInCategory;
    }
}
