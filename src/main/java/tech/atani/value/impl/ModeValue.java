package tech.atani.value.impl;

import tech.atani.module.Module;
import tech.atani.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ModeValue extends Value {
    public int index;
    public List<String> modes;
    public String currentMode;
    public int index2;

    public ModeValue(String name, Module parent, String current, String... modes) {
        this.name = name;
        try {
            this.modes = Arrays.asList(modes);
            index = this.modes.indexOf(current);
            this.currentMode = this.modes.get(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            this.modes = Arrays.asList(modes);
            index = this.modes.indexOf(current);
            this.currentMode = this.modes.get(0);
        }
        this.visible = () -> true;
        this.save = () -> true;
        parent.getValues().add(this);
    }

    public ModeValue(String name, Module parent, String current, Supplier<Boolean> visible, String... modes) {
        this.name = name;
        try {
            this.modes = Arrays.asList(modes);
            index = this.modes.indexOf(current);
            this.currentMode = this.modes.get(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            this.modes = Arrays.asList(modes);
            index = this.modes.indexOf(current);
            this.currentMode = this.modes.get(0);
        }
        this.visible = visible;
        this.save = () -> true;
        parent.getValues().add(this);
    }

    public void setMode(String mode) {
        this.currentMode = mode;
        this.index = this.modes.indexOf(mode);
    }

    public String getMode() {
        try {
            return modes.get(index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return modes.get(0);
        }
    }

    public boolean is(String mode) {
        return index == modes.indexOf(mode);
    }

    public void setListMode(String selected) {
        this.currentMode = selected;
        this.index = this.modes.indexOf(selected);
    }

    public List<String> getModes() {
        return modes;
    }

    public void positiveCycle() {
        if (this.index < this.modes.size() - 1) {
            this.index++;
        } else {
            this.index = 0;
        }
    }

    public void negativeCycle() {
        if (this.index <= 0) {
            this.index = this.modes.size() - 1;
        } else {
            this.index--;
        }
    }

    public String[] getModes2() {
        return modes.toArray(new String[0]);
    }
}