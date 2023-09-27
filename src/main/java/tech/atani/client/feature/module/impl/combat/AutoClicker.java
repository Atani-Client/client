package tech.atani.client.feature.module.impl.combat;

import com.google.common.base.Supplier;
import org.lwjgl.input.Mouse;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.math.time.TimeHelper;

import java.util.Random;

@ModuleData(name = "AutoClicker", description = "clicks for you", category = Category.COMBAT)
public class AutoClicker extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mouse Mode", "Which mode will the module use?", this, new String[] {"Left", "Right"});
    private final SliderValue<Integer> cps = new SliderValue<Integer>("CPS", "How much cps will the clicker make?", this, 12, 0, 30, 0);
    private final CheckBoxValue hold = new CheckBoxValue("Hold", "Should the clicker only work when holding down the mouse?", this, false);
    private final CheckBoxValue allowBlockHit = new CheckBoxValue("Allow Block-Hitting", "Should the clicker allow block hitting?", this, false);
    private final CheckBoxValue randomise = new CheckBoxValue("Randomise", "Should the module randomise CPS?", this, false);
    private final SliderValue<Integer> randomisedValue = new SliderValue<Integer>("Randomizer Amount", "How much random will the CPS be?", this, 3, 0, 7, 0, new Supplier[]{randomise::getValue});

    private final TimeHelper timer = new TimeHelper();


    public void click() {
        if (hold.getValue()) {
            switch (mode.getValue()) {
                case "Right":
                    if (Mouse.isButtonDown(1))
                        mc.rightClickMouse();
                    break;
                case "Left":
                    if (Mouse.isButtonDown(0))
                        mc.clickMouse();
                    break;
            }
        } else {
            switch (mode.getValue()) {
                case "Right":
                    mc.rightClickMouse();
                    break;
                case "Left":
                    mc.clickMouse();
                    break;
            }
        }
    }

    @Listen
    public void onMotion(UpdateMotionEvent event) {
        Random random = new Random();
        int currentSpeed = cps.getValue();
        int newSpeed;

        if (randomise.getValue()) {
            int randomNum = random.nextInt(5);
            newSpeed = (currentSpeed + randomNum - randomisedValue.getValue());
        } else {
            newSpeed = currentSpeed;
        }

        int maxSpeed = cps.getMaximum();

        if (newSpeed > maxSpeed) {
            newSpeed = maxSpeed;
        } else if (newSpeed < 0) {
            newSpeed = 0;
        }


        if(mc.currentScreen != null)
            return;
        if(allowBlockHit.getValue()) {
            if (timer.hasReached((1000 / newSpeed), true)) {
                click();
            }
        } else {
            if(!mc.thePlayer.isBlocking() && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isEating()) {
                if (timer.hasReached((1000 / newSpeed), true)) {
                    click();
                }
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
