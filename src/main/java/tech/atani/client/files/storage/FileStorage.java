package tech.atani.client.files.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.florianmichael.rclasses.storage.Storage;
import tech.atani.client.files.impl.SessionFile;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.files.IFile;
import tech.atani.client.files.impl.AccountsFile;
import tech.atani.client.files.impl.ModulesFile;

import java.io.File;

public class FileStorage extends Storage<IFile> implements Methods {

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    private File root;

    private static FileStorage instance;

    @Override
    public void init() {
        this.setupRoot(CLIENT_NAME);
        this.add(new ModulesFile());
        this.add(new AccountsFile());
        this.add(new SessionFile());
        this.load();
    }

    public void add(IFile item) {
        item.setFile(root);
        super.add(item);
    }

    public void saveFile(Class<? extends IFile> iFile) {
        IFile file = getByClass(iFile);
        if (file != null) {
            file.save(GSON);
        }
    }

    public void loadFile(Class<? extends IFile> iFile) {
        IFile file = getByClass(iFile);
        if (file != null) {
            file.load(GSON);
        }
    }

    public void save() {
        this.getList().forEach(file -> file.save(GSON));
    }

    public void load() {
        this.getList().forEach(file -> file.load(GSON));
    }

    public void setupRoot(String name) {
        // make the root directory
        root = new File(mc.mcDataDir, name);

        // check if it exists if not make it
        if (!root.exists()) {
            if (!root.mkdirs()) {
                mc.logger.warn("Failed to create the root folder \"" + root.getPath() + "\".");
            }
        }
    }

    public static FileStorage getInstance() {
        return instance;
    }

    public static void setInstance(FileStorage instance) {
        FileStorage.instance = instance;
    }
}
