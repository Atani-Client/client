package wtf.atani.value.storage;

import de.florianmichael.rclasses.storage.Storage;
import wtf.atani.module.Module;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.Value;

import java.util.ArrayList;

public class ValueStorage extends Storage<Value> {

    private static ValueStorage instance;

    public ValueStorage() {
        instance = this;
        this.init();
    }

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
}
