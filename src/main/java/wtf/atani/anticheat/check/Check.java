package wtf.atani.anticheat.check;

import wtf.atani.anticheat.data.PlayerData;
import wtf.atani.utils.interfaces.Methods;

public class Check implements Methods {
    private final String name;

    private final PlayerData data;

    public Check(String name, PlayerData data) {
        this.name = name;
        this.data = data;
    }

    public void fail() {
        this.sendMessage(data.getPlayer().getDisplayName() + " failed " + name);
    }

    public String getName() {
        return name;
    }
}
