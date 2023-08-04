package wtf.atani.module.impl.player;

import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;

@ModuleInfo(name = "AutoRespawn", description = "Automatically respawn after dying.", category = Category.PLAYER)
public class AutoRespawn extends Module {

    private final CheckBoxValue instant = new CheckBoxValue("Instant", "Do you want to respawn faster?", this, true);

    @Listen
    public void onMotionEvent(UpdateMotionEvent event) {
        if(event.getType() == UpdateMotionEvent.Type.MID) {
            if (this.isDead())
                mc.thePlayer.respawnPlayer();
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    private boolean isDead() {
        return instant.getValue() ? mc.thePlayer.getHealth() <= 0 : mc.thePlayer.isDead;
    }

}