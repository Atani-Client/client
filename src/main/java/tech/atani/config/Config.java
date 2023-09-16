package tech.atani.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import tech.atani.Client;
import tech.atani.module.Module;
import tech.atani.utils.misc.FileUtil;
import tech.atani.utils.interfaces.IMethods;
import tech.atani.value.Value;
import tech.atani.value.impl.BooleanValue;
import tech.atani.value.impl.ModeValue;
import tech.atani.value.impl.NumberValue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config implements IMethods {

    private final File directory = new File(mc.mcDataDir, "/Atani/Configs");

    private final String name;

    public String getName() {
        return name;
    }

    private boolean saveKeybinds;

    public Config(String name, boolean saveBinds) {
        this.name = name;
        this.saveKeybinds = saveBinds;
    }

    public void write() {
        JsonObject jsonObject = new JsonObject();
        Client.INSTANCE.getModuleManager().forEach(m -> {
            JsonObject mObject = new JsonObject();
            mObject.addProperty("state", m.isToggled());

            if (saveKeybinds)
                mObject.addProperty("bind", m.getKeyBind());

            JsonObject vObject = new JsonObject();
            m.getValues().forEach(v -> {
                if (v instanceof BooleanValue)
                    vObject.addProperty(v.name, ((BooleanValue) v).isToggled());

                if (v instanceof ModeValue)
                    vObject.addProperty(v.name, ((ModeValue) v).getMode());

                if (v instanceof NumberValue)
                    vObject.addProperty(v.name, ((NumberValue) v).getValue());
            });

            mObject.add("values", vObject);
            jsonObject.add(m.getName(), mObject);
        });
        FileUtil.writeJsonToFile(jsonObject, new File(directory, name + ".json").getAbsolutePath());
    }

    public void read() {
        JsonObject jsonObject = FileUtil.readJsonFromFile(new File(directory, name + ".json").getAbsolutePath());
        List<Module> okok = new ArrayList<>(Client.INSTANCE.getModuleManager());

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            for (Module m : okok) {
                if (entry.getKey().equalsIgnoreCase(m.getName())) {
                    JsonObject jsonObject1 = (JsonObject) entry.getValue();
                    m.setToggled(jsonObject1.get("state").getAsBoolean());

                    if (jsonObject1.has("bind"))
                        m.setKeyBind(jsonObject1.get("bind").getAsInt());

                    JsonObject values = jsonObject1.get("values").getAsJsonObject();
                    for (Map.Entry<String, JsonElement> value : values.entrySet()) {
                        if (m.getValueByName(value.getKey()) != null) {
                            try {
                                Value v = m.getValueByName(value.getKey());

                                if (v instanceof BooleanValue)
                                    ((BooleanValue) v).setToggled(value.getValue().getAsBoolean());

                                if (v instanceof ModeValue)
                                    ((ModeValue) v).setMode(value.getValue().getAsString());

                                if (v instanceof NumberValue)
                                    ((NumberValue) v).setValue(value.getValue().getAsDouble());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}