package wtf.atani.module.impl.movement;

import wtf.atani.event.events.TickEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.player.PlayerHandler;
import wtf.atani.value.impl.CheckBoxValue;

@ModuleInfo(name = "CorrectMovement", description = "Corrects your movement according to your yaw", category = Category.MOVEMENT, alwaysEnabled = true)
public class CorrectMovement extends Module {

    public CheckBoxValue moveFixSilent = new CheckBoxValue("Silent", "Silently fix your movement?", this, true);

    @Listen
    public final void onTick(TickEvent tickEvent) {
        PlayerHandler.moveFix = this.isEnabled();
        PlayerHandler.moveFixSilent = this.isEnabled() && moveFixSilent.getValue();
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
