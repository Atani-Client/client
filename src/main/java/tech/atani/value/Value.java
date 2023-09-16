package tech.atani.value;

import java.util.function.Supplier;

public class Value {
    public String name;
    protected Supplier<Boolean> visible;
    protected Supplier<Boolean> save;

    public boolean isVisible() {
        return visible.get();
    }

    public boolean canSave() {
        return save.get();
    }

    public String getName() {
        return name;
    }
}
