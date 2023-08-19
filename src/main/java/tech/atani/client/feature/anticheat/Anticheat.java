package tech.atani.client.feature.anticheat;

import tech.atani.client.feature.anticheat.data.PlayerData;

import java.util.HashMap;
import java.util.UUID;

public class Anticheat {
    private final HashMap<UUID, PlayerData> dataMap = new HashMap<>();

    public PlayerData getData(UUID uuid) {
        return dataMap.get(uuid);
    }

    public HashMap<UUID, PlayerData> getDataMap() {
        return dataMap;
    }

}
