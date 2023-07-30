package wtf.atani.anticheat.data;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.Packet;

public class PlayerData {

    private final EntityLivingBase player;

    public PlayerData(EntityLivingBase player) {
        this.player = player;
    }

    public void process(Packet<?> packet) {

    }

    public EntityLivingBase getPlayer() {
        return player;
    }

}
