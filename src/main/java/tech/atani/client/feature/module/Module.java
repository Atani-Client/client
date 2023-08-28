package tech.atani.client.feature.module;

import com.google.gson.JsonObject;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.listener.event.client.DisableModuleEvent;
import tech.atani.client.listener.event.client.EnableModuleEvent;
import tech.atani.client.listener.handling.EventHandling;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.feature.value.Value;
import tech.atani.client.feature.value.storage.ValueStorage;

import java.util.List;

public abstract class Module implements Methods {

    private final String name, description;
    private final Category category;
    private int key;
    private boolean enabled;
    private boolean alwaysRegistered;
    private boolean frozenState;

    public Module() {
        ModuleData moduleData = this.getClass().getAnnotation(ModuleData.class);
        if(moduleData == null)
            throw new RuntimeException();
        this.name = moduleData.name();
        this.description = moduleData.description();
        this.category = moduleData.category();
        this.key = moduleData.key();
        this.enabled = moduleData.enabled();

        if(moduleData.alwaysRegistered()) {
            EventHandling.getInstance().registerListener(this);
            alwaysRegistered = true;
        }

        if(moduleData.frozenState()) {
            this.enabled = true;
            frozenState = true;
        }
    }

    public void toggle() {
        this.setEnabled(!this.isEnabled());
    }

    public void setEnabled(boolean enabled) {
        if(this.frozenState) {
            enabled = false;
        }

        this.enabled = enabled;
        if(enabled) {
            onModuleEnable();
        } else {
            onModuleDisable();
        }
    }

    private void onModuleEnable() {
        EnableModuleEvent enableModuleEvent = new EnableModuleEvent(this, EnableModuleEvent.Type.PRE).publishItself();
        if(enableModuleEvent.isCancelled())
            return;
        onEnable();
        if(!alwaysRegistered)
            EventHandling.getInstance().registerListener(this);
        new EnableModuleEvent(this, EnableModuleEvent.Type.POST).publishItself();
    }

    private void onModuleDisable() {
        DisableModuleEvent disableModuleEvent = new DisableModuleEvent(this, DisableModuleEvent.Type.PRE).publishItself();
        if(disableModuleEvent.isCancelled())
            return;
        if(!alwaysRegistered)
            EventHandling.getInstance().unregisterListener(this);
        onDisable();
        new DisableModuleEvent(this, DisableModuleEvent.Type.POST).publishItself();
    }

    public abstract void onEnable();
    public abstract void onDisable();
    
    public String getSuffix() {
		return null;
	}

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

}
