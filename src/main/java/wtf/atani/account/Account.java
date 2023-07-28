package wtf.atani.account;

import com.google.gson.JsonObject;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import wtf.atani.account.thread.AltLoginThread;
import wtf.atani.utils.crypt.CryptUtil;
import wtf.atani.value.Value;
import wtf.atani.value.storage.ValueStorage;

import java.util.List;
import java.util.UUID;

public class Account {
    private String name, password;
    private boolean cracked;

    public Account(String name, String password, boolean cracked) {
        this.name = name;
        this.password = password;
        this.cracked = cracked;
    }

    public void login() {
        new AltLoginThread(this).run();
    }

    public JsonObject save() {
        JsonObject object = new JsonObject();
        object.addProperty("Password", password);
        object.addProperty("Cracked", cracked);
        return object;
    }

    public void load(JsonObject object) {
        if (object.has("Password"))
            setPassword(object.get("Password").getAsString());
        if (object.has("Cracked"))
            setCracked(object.get("Cracked").getAsBoolean());
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isCracked() {
        return cracked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCracked(boolean cracked) {
        this.cracked = cracked;
    }
}
