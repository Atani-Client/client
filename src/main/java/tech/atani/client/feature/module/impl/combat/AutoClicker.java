package tech.atani.client.feature.module.impl.combat;

import com.google.common.base.Supplier;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.math.time.TimeHelper;

@ModuleData(name = "AutoClicker", description = "clicks for you", category = Category.COMBAT)
public class AutoClicker extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mouse Mode", "Which mode will the module use?", this, new String[] {"Left", "Right"});
    private final SliderValue<Integer> cps = new SliderValue<>("CPS", "How many times will the module click per second?", this, 12, 1, 30, 1);
    private final CheckBoxValue allowBlockHit = new CheckBoxValue("Allow Block-Hitting", "Should the clicker allow block hitting?", this, false);
    private final CheckBoxValue randomise = new CheckBoxValue("Randomise", "Should the module randomise CPS?", this, false);
    private final SliderValue<Integer> randomisedValue = new SliderValue<Integer>("Randomizer Amount", "How much random will the CPS be?", this, 3, 0, 7, 1, new Supplier[]{randomise::getValue});

    private final TimeHelper timer = new TimeHelper();

    public void click() {
        switch (mode.getValue()) {
            case "Right":
                mc.rightClickMouse();
                break;
            case "Left":
                mc.clickMouse();
                break;
        }
    }

    @Listen
    public void onMotion(UpdateMotionEvent event) {
        int newCPS = (int) ((cps.getValue() + (Math.random() * randomisedValue.getValue())) - (Math.random() * randomisedValue.getValue()));

        if(newCPS < 0 || newCPS > 30) {
            newCPS = cps.getValue();
        }

//        PlayerUtil.addChatMessgae("CPS: " + newCPS,true);
        if (mc.currentScreen == null && event.getType() == UpdateMotionEvent.Type.MID && mc.gameSettings.keyBindAttack.pressed) {
            if (allowBlockHit.getValue()) {
                if (timer.hasReached((1000 / newCPS), true)) {
                    click();
                }
            } else {
                if (!mc.thePlayer.isBlocking() && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isEating()) {
                    if (timer.hasReached((1000 / newCPS), true)) {
                        click();
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
