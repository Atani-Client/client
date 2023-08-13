package wtf.atani.processor.storage;

import de.florianmichael.rclasses.storage.Storage;
import org.reflections.Reflections;
import wtf.atani.event.events.KeyInputEvent;
import wtf.atani.event.handling.EventHandling;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.processor.Processor;
import wtf.atani.processor.data.ProcessorInfo;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class ProcessorStorage extends Storage<Processor> {

    private static ProcessorStorage instance;

    public ProcessorStorage() {
        instance = this;
        init();
    }

    @Override
    public void init() {
        EventHandling.getInstance().registerListener(this);
        final Reflections reflections = new Reflections("wtf.atani");
        reflections.getTypesAnnotatedWith(ProcessorInfo.class).forEach(aClass -> {
            try {
                Processor processor = (Processor) aClass.getDeclaredConstructor().newInstance();
                this.add(processor);
                processor.launch();
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public <V extends Processor> V getByClass(final Class<V> clazz) {
        final Processor processor = this.getList().stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
        if (processor == null) return null;
        return clazz.cast(processor);
    }

    public static ProcessorStorage getInstance() {
        return instance;
    }
}
