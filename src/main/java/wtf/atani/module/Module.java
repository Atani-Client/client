package wtf.atani.module;

import com.google.gson.JsonObject;
import wtf.atani.event.events.DisableModuleEvent;
import wtf.atani.event.events.EnableModuleEvent;
import wtf.atani.event.handling.EventHandling;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.interfaces.Methods;
import wtf.atani.value.Value;
import wtf.atani.value.storage.ValueStorage;

import java.util.List;

public abstract class Module implements Methods {

    private final String name, description;
    private final Category category;
    private int key;
    private boolean enabled;
    private boolean alwaysEnabled;
    private boolean listening;

    public Module() {
        ModuleInfo moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
        if(moduleInfo == null)
            throw new RuntimeException();
        this.name = moduleInfo.name();
        this.description = moduleInfo.description();
        this.category = moduleInfo.category();
        this.key = moduleInfo.key();

        if(moduleInfo.alwaysEnabled()) {
            EventHandling.getInstance().registerListener(this);
            alwaysEnabled = true;
        }
    }

    public void toggle() {
        this.setEnabled(!this.isEnabled());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if(enabled) {
            onModuleEnable();
        } else {
            onModuleDisable();
        }
    }

    private void onModuleEnable() {
        EnableModuleEvent enableModuleEvent = new EnableModuleEvent(this, EnableModuleEvent.Type.PRE).onFire();
        if(enableModuleEvent.isCancelled())
            return;
        onEnable();
        if(!alwaysEnabled)
            EventHandling.getInstance().registerListener(this);
        new EnableModuleEvent(this, EnableModuleEvent.Type.POST).onFire();
    }

    private void onModuleDisable() {
        DisableModuleEvent disableModuleEvent = new DisableModuleEvent(this, DisableModuleEvent.Type.PRE).onFire();
        if(disableModuleEvent.isCancelled())
            return;
        if(!alwaysEnabled)
            EventHandling.getInstance().unregisterListener(this);
        onDisable();
        new DisableModuleEvent(this, DisableModuleEvent.Type.POST).onFire();
    }

    public abstract void onEnable();
    public abstract void onDisable();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public int getKey() {
        return key;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public JsonObject save() {
        JsonObject object = new JsonObject();
        object.addProperty("Enabled", isEnabled());
        object.addProperty("Key", getKey());
        List<Value> values = ValueStorage.getInstance().getValues(this);
        if (values != null && !values.isEmpty()) {
            JsonObject propertiesObject = new JsonObject();
            for (Value property : values) {
                propertiesObject.addProperty(property.getIdName(), property.getValueAsString());
            }
            object.add("Values", propertiesObject);
        }
        return object;
    }

    public void load(JsonObject object) {
        try {
            if (object.has("Enabled"))
                setEnabled(object.get("Enabled").getAsBoolean());
        } catch (Exception e) {

        }

        if (object.has("Key"))
            setKey(object.get("Key").getAsInt());

        List<Value> values = ValueStorage.getInstance().getValues(this);

        if (object.has("Values") && values != null && !values.isEmpty()) {
            JsonObject propertiesObject = object.getAsJsonObject("Values");
            for (Value property : values) {
                if (propertiesObject.has(property.getIdName())) {
                    property.setValue(propertiesObject.get(property.getIdName()).getAsString());
                }
            }
        }
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }
}
