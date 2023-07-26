package wtf.atani.module.impl.player;

import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "AutoRespawn", description = "Automatically respawn after dying.", category = Category.PLAYER)
public class AutoRespawn extends Module {

    @Listen
    public void onMotionEvent(UpdateMotionEvent event) {
        if(event.getType() == UpdateMotionEvent.Type.PRE) {
            if (mc.thePlayer.isDead) {
                mc.thePlayer.respawnPlayer();
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}