package wtf.atani.module.impl.player;

import net.minecraft.util.MovingObjectPosition;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "Fast Break", description = "Break blocks faster", category = Category.PLAYER)
public class FastBreak extends Module {

    private final SliderValue<Float> speed = new SliderValue<>("Speed", "How fast should the breaking speed be?", this, 4F, 0F, 10F, 1);

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        if(mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && isKeyDown(mc.gameSettings.keyBindAttack.getKeyCode())) {
            if(mc.playerController.curBlockDamageMP > 1 - (speed.getValue().floatValue() / 10)) {
                mc.playerController.curBlockDamageMP = 1;
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
