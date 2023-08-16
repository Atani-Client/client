package wtf.atani.module.impl.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.atani.event.events.MoveEntityEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.world.BlockUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "AntiVoid", description = "Tries to make you not fall in the void.", category = Category.PLAYER)
public class AntiVoid extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, (new String[] {"Flag", "Position", "Ground"}));
    private final CheckBoxValue voidCheck = new CheckBoxValue("Void Check", "Should the module check for void?", this, true);

    @Override
    public String getSuffix() { return mode.getValue(); }

    @Listen
    public void onMove(MoveEntityEvent event) {
        if((!BlockUtil.isBlockUnderPlayer() || voidCheck.getValue() && mc.thePlayer.fallDistance > 5 && !mc.gameSettings.keyBindSneak.isKeyDown() && !mc.thePlayer.capabilities.isFlying)) {
            switch(mode.getValue()) {
                case "Flag":
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition());
                    break;
                case "Position":
                    event.setY(event.getY() + mc.thePlayer.fallDistance);
                    break;
                case "Ground":
                    mc.thePlayer.onGround = true;
                    break;

            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}