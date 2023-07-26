package wtf.atani.module.storage;

import de.florianmichael.rclasses.storage.Storage;
import org.reflections.Reflections;
import wtf.atani.event.events.KeyInputEvent;
import wtf.atani.event.handling.EventHandling;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ModuleStorage extends Storage<Module> {

    private static ModuleStorage instance;

    public ModuleStorage() {
        instance = this;
        init();
    }

    @Override
    public void init() {
        EventHandling.getInstance().registerListener(this);
        final Reflections reflections = new Reflections("wtf.atani");
        reflections.getTypesAnnotatedWith(ModuleInfo.class).forEach(aClass -> {
            try {
                this.add((Module) aClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    @Listen
    public void onKey(KeyInputEvent keyInputEvent) {
        this.getList()
                .stream()
                .filter(module -> module.getKey() == keyInputEvent.getKey())
                .forEach(Module::toggle);
    }

    @Override
    public <V extends Module> V getByClass(final Class<V> clazz) {
        final Module feature = this.getList().stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
        if (feature == null) return null;
        return clazz.cast(feature);
    }

    public final ArrayList<Module> getModules(Category category) {
        ArrayList<Module> modules = new ArrayList<>();
        for (Module m : this.getList()) {
            if (m.getCategory() == category)
                modules.add(m);
        }
        return modules;
    }

    public static ModuleStorage getInstance() {
        return instance;
    }
}
