package wtf.atani.value.interfaces;

import wtf.atani.value.Value;

public interface ValueChangeListener {
    void onChange(Stage stage, Value value, Object oldValue, Object newValue);

    enum Stage {
        PRE, POST;
    }
}
