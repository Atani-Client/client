package wtf.atani.module.impl.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.world.WorldSettings;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

import java.util.UUID;

@ModuleInfo(name = "FakePlayer", description = "Spawns in a fake player to be used for testing", category = Category.MISCELLANEOUS)
public class FakePlayer extends Module {

    @Override
    public void onEnable() {
        if(mc.thePlayer == null || mc.thePlayer.isDead){
            setEnabled(false);
        }

        EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(UUID.fromString("9b7f28c2-98ea-4d70-b2db-48e6c78a4a9d"), mc.session.getUsername()));
        clonedPlayer.copyLocationAndAnglesFrom(mc.thePlayer);
        clonedPlayer.rotationYawHead = mc.thePlayer.rotationYawHead;
        clonedPlayer.rotationYaw = mc.thePlayer.rotationYaw;
        clonedPlayer.rotationPitch = mc.thePlayer.rotationPitch;
        clonedPlayer.setGameType(WorldSettings.GameType.SURVIVAL);
        clonedPlayer.setHealth(20);
        mc.theWorld.addEntityToWorld(-4200, clonedPlayer);
        clonedPlayer.onLivingUpdate();
    }

    @Override
    public void onDisable() {
        if (mc.theWorld != null) {
            mc.theWorld.removeEntityFromWorld(-4200);
        }
    }

}