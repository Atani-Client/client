package wtf.atani.module.impl.movement;

import net.minecraft.client.settings.KeyBinding;
import wtf.atani.event.events.DirectionSprintCheckEvent;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.value.impl.CheckBoxValue;

@ModuleInfo(name = "Sprint", description = "Makes you sprint automatically.", category = Category.MOVEMENT)
public class Sprint extends Module {

    public CheckBoxValue legit = new CheckBoxValue("Legit", "Sprint legit?", this, false);
    public CheckBoxValue omni = new CheckBoxValue("All Directions", "Sprint in all directions?", this, false);

    @Listen
    public final void onMotion(UpdateMotionEvent updateMotionEvent) {
        if (legit.getValue()) {
            getGameSettings().keyBindSprint.pressed = true;
    } else {
            if (MoveUtil.getSpeed() != 0) {
                getPlayer().setSprinting(true);
            }
        }
    }

    @Listen
    public final void onOmniCheck(DirectionSprintCheckEvent directionSprintCheckEvent) {
        if(omni.getValue()) {
            if(MoveUtil.getSpeed() != 0) {
                directionSprintCheckEvent.setSprintCheck(false);
            }
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
    }
}
