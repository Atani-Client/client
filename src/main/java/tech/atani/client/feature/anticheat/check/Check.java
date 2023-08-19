package tech.atani.client.feature.anticheat.check;

import tech.atani.client.feature.anticheat.data.PlayerData;
import tech.atani.client.utility.interfaces.Methods;

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
