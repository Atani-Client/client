package tech.atani.value.impl;

import tech.atani.module.Module;
import tech.atani.value.Value;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class BooleanValue extends Value {
    private boolean toggled;

    public BooleanValue(String name, Module parent, boolean val) {
        this.name = name;
        this.toggled = val;
        this.visible = () -> true;
        this.save = () -> true;
        parent.getValues().add(this);
    }

    public BooleanValue(String name, Module parent, boolean val, Supplier<Boolean> visible) {
        this.name = name;
        this.toggled = val;
        this.visible = visible;
        this.save = () -> true;
        parent.getValues().add(this);
    }

    public boolean isToggled() {
        return toggled;
    }

    public void toggle() {
        this.toggled = !toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
}