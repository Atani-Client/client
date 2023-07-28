package wtf.atani.module.impl.combat;

import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "TriggerBot", description = "Automatically clicks when holding down the attack button", category = Category.COMBAT)
public class TriggerBot extends Module {

    private final SliderValue<Integer> cps = new SliderValue<>("CPS", "How many cps will the client click?", this, 12, 1, 24, 1);

    private final TimeHelper timer = new TimeHelper();

    @Listen
    public final void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {

        int randomizedCps = (int) ((cps.getValue() + Math.round(Math.random() / 6)) - Math.round(Math.random() / 8));
        boolean doubleClick;

        doubleClick = Math.random() * 100 < 33;

        if(mc.pointedEntity != null) {
            if(timer.hasReached(1000 / randomizedCps, true)) {
                mc.clickMouse();
                if(doubleClick) mc.clickMouse();
            }
        }

    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}