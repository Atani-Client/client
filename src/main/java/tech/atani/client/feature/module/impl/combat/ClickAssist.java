package tech.atani.client.feature.module.impl.combat;

import cn.muyang.nativeobfuscator.Native;
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
import tech.atani.client.utility.player.PlayerUtil;

@Native
@ModuleData(name = "ClickAssist", description = "Adds extra clicks", category = Category.COMBAT)
public class ClickAssist extends Module {
    private final SliderValue<Integer> chance = new SliderValue<>("Chance", "What should the chance to doubleclick be?", this, 50, 10, 100, 1);

    private int clickStage = 0;

    public void click() {
         mc.clickMouse();
    }

    @Listen
    public void onMotion(UpdateMotionEvent event) {
        if(clickStage >= 5) {
            if(chance.getValue() > Math.random() * 100 && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isEating() && !mc.thePlayer.isBlocking()) {
                click();
            }
            clickStage = 0;
        }

        if(mc.gameSettings.keyBindAttack.pressed) {
            clickStage = 1;
        } else if (clickStage >= 1) {
            clickStage += 1;
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
