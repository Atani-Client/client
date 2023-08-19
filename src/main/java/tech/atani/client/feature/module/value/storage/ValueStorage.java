package tech.atani.client.feature.module.value.storage;

import de.florianmichael.rclasses.storage.Storage;
import tech.atani.client.feature.module.value.Value;

import java.util.ArrayList;

public class ValueStorage extends Storage<Value> {

    private static ValueStorage instance;

    @Override
    public void init() {

    }

    public final ArrayList<Value> getValues(Object owner) {
        ArrayList<Value> values = new ArrayList<>();
        for (Value v : this.getList()) {
            if (v.getOwner() == owner)
                values.add(v);
        }
        return values;
    }

    public static ValueStorage getInstance() {
        return instance;
    }

    public static void setInstance(ValueStorage instance) {
        ValueStorage.instance = instance;
    }
}
