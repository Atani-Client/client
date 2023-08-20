package tech.atani.client.files.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import tech.atani.client.files.IFile;
import tech.atani.client.processor.storage.ProcessorStorage;
import tech.atani.client.processor.impl.SessionProcessor;

import java.io.File;

public class SessionFile implements IFile {

    private File file;
    private SessionProcessor sessionProcessor;

    @Override
    public void save(Gson gson) {
        if(sessionProcessor == null)
            sessionProcessor = ProcessorStorage.getInstance().getByClass(SessionProcessor.class);

        if(!sessionProcessor.shouldSave())
            return;

        JsonObject object = new JsonObject();

        JsonObject sessionObject = new JsonObject();

        sessionObject.addProperty("Kills", sessionProcessor.getKills());
        sessionObject.addProperty("Wins", sessionProcessor.getWins());
        sessionObject.addProperty("Deaths", sessionProcessor.getDeaths());

        object.add("Session", sessionObject);

        writeFile(gson.toJson(object), file);
    }

    @Override
    public void load(Gson gson) {
        if(sessionProcessor == null)
            sessionProcessor = ProcessorStorage.getInstance().getByClass(SessionProcessor.class);

        if(!sessionProcessor.shouldSave())
            return;

        if (!file.exists()) {
            return;
        }

        JsonObject object = gson.fromJson(readFile(file), JsonObject.class);
        if (object.has("Session")){
            JsonObject sessionObject = object.getAsJsonObject("Session");
            sessionProcessor.setKills(sessionObject.get("Kills").getAsInt());
            sessionProcessor.setWins(sessionObject.get("Wins").getAsInt());
            sessionProcessor.setDeaths(sessionObject.get("Deaths").getAsInt());
        }
    }

    @Override
    public void setFile(File root) {
        file = new File(root, "/session.json");
    }
}