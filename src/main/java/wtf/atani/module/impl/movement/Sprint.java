package wtf.atani.module.impl.movement;

import net.minecraft.client.settings.KeyBinding;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;

@ModuleInfo(name = "Sprint", description = "Makes you sprint automatically.", category = Category.MOVEMENT)
public class Sprint extends Module {

    public CheckBoxValue omni = new CheckBoxValue("Omni Sprint", "Sprint to all sides.", this, false);

    @Listen
    public final void onMotion(UpdateMotionEvent updateMotionEvent) {
        if(omni.getValue()) {
            mc.thePlayer.setSprinting(true);
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
