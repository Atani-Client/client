package wtf.atani.value.impl;

import com.google.common.base.Supplier;
import wtf.atani.value.Value;
import wtf.atani.value.interfaces.ValueChangeListener;

public class StringBoxValue extends Value<String> {

    private final String[] values;

    public StringBoxValue(String name, String description, Object owner, String values[], ValueChangeListener[] valueChangeListeners, Supplier<Boolean>[] suppliers) {
        super(name, description, owner, values[0], valueChangeListeners, suppliers);
        this.values = values;
    }

    public StringBoxValue(String name, String description, Object owner, String values[], Supplier<Boolean>[] suppliers) {
        super(name, description, owner, values[0], null, suppliers);
        this.values = values;
    }

    public StringBoxValue(String name, String description, Object owner, String values[], ValueChangeListener[] valueChangeListeners) {
        super(name, description, owner, values[0], valueChangeListeners, null);
        this.values = values;
    }

    public StringBoxValue(String name, String description, Object owner, String values[]) {
        super(name, description, owner, values[0]);
        this.values = values;
    }

    public boolean is(String string) {
        return this.getValue().equalsIgnoreCase(string);
    }

    @Override
    public String getValueAsString() {
        return getValue();
    }

    @Override
    public void setValue(String string) {
        this.value = string;
    }

    public String[] getValues() {
        return values;
    }
}
