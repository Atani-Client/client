package wtf.atani.command.storage;

import de.florianmichael.rclasses.storage.Storage;
import org.reflections.Reflections;
import wtf.atani.command.Command;
import wtf.atani.command.data.CommandInfo;
import wtf.atani.event.events.KeyInputEvent;
import wtf.atani.event.handling.EventHandling;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.enums.Category;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class CommandStorage extends Storage<Command> {

    private static CommandStorage instance;

    public CommandStorage() {
        instance = this;
        init();
    }

    @Override
    public void init() {
        EventHandling.getInstance().registerListener(this);
        final Reflections reflections = new Reflections("wtf.atani");
        reflections.getTypesAnnotatedWith(CommandInfo.class).forEach(aClass -> {
            try {
                this.add((Command) aClass.getDeclaredConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    public static CommandStorage getInstance() {
        return instance;
    }
}
