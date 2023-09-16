package tech.atani.config;

import tech.atani.utils.interfaces.IMethods;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigManager implements IMethods {

    private final Map<String, Config> configs = new HashMap<>();

    public Map<String, Config> getConfigs() {
        return configs;
    }

    private final File configFolder = new File(mc.mcDataDir, "/Atani/Configs");

    private Config activeConfig;

    public Config getActiveConfig() {
        return activeConfig;
    }

    public void init() {
        if (!configFolder.exists())
            configFolder.mkdirs();

        for (File file : Objects.requireNonNull(configFolder.listFiles())) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                String name = file.getName().replaceAll(".json", "");
                Config config = new Config(name, true);
                configs.put(config.getName(), config);
            }
        }

        if (getConfig("default") == null) {
            Config config = new Config("default", true);
            config.write();
            configs.put(config.getName(), config);
        } else getConfig("default").read();
    }

    public void stop() {
        if (getConfig("default") == null) {
            Config config = new Config("default", true);
            config.write();
        } else getConfig("default").write();
    }

    public Config getConfig(String name) {
        return configs.keySet().stream().filter(key -> key.equalsIgnoreCase(name)).findFirst().map(configs::get).orElse(null);
    }

    public void saveConfig(String configName){
        if (getConfig(configName) == null) {
            Config config = new Config(configName, false);
            config.write();
        } else getConfig(configName).write();

    }

    public void loadConfig(String configName){
        if (getConfig(configName) != null)
            getConfig(configName).read();
    }
}