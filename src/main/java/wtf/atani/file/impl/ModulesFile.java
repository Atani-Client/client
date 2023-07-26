package wtf.atani.file.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import wtf.atani.file.IFile;
import wtf.atani.module.Module;
import wtf.atani.module.storage.ModuleStorage;

import java.io.File;

public class ModulesFile implements IFile {

    private File file;

    @Override
    public void save(Gson gson) {
        JsonObject object = new JsonObject();

        JsonObject modulesObject = new JsonObject();

        for (Module module : ModuleStorage.getInstance().getList())
            modulesObject.add(module.getName(), module.save());

        object.add("Modules", modulesObject);

        writeFile(gson.toJson(object), file);
    }

    @Override
    public void load(Gson gson) {
        if (!file.exists()) {
            return;
        }

        JsonObject object = gson.fromJson(readFile(file), JsonObject.class);
        if (object.has("Modules")){
            JsonObject modulesObject = object.getAsJsonObject("Modules");

            for (Module module : ModuleStorage.getInstance().getList()) {
                if (modulesObject.has(module.getName()))
                    module.load(modulesObject.getAsJsonObject(module.getName()));
            }
        }
    }

    @Override
    public void setFile(File root) {
        file = new File(root, "/modules.json");
    }
}