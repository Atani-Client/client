package wtf.atani.module.impl.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "Parkour", description = "Makes you jump on the edge of blocks.", category = Category.MOVEMENT)
public class Parkour extends Module {

    private final SliderValue<Integer> delay = new SliderValue<>("Jump Delay", "How big will the delay be to jump?", this, 30, 0, 300, 0);

    private final TimeHelper timer = new TimeHelper();

    @Listen
    public final void onMotion(UpdateMotionEvent updateMotionEvent) {
        if (updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
            if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ)).getBlock() instanceof BlockAir && mc.thePlayer.onGround) {
                if(timer.hasReached(delay.getValue(), true)) {
                    mc.thePlayer.jump();
                }
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
