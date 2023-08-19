package wtf.atani.module.impl.movement;

import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import wtf.atani.event.events.CollisionBoxesEvent;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "Jesus", description = "Walk on water like jesus", category = Category.MOVEMENT)
public class Jesus extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "What mode should this module use?", this, new String[] {"Solid", "Matrix"});

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Listen
    public void onMotion(UpdateMotionEvent updateMotionEvent) {
        if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
            switch(mode.getValue()) {
            case "Matrix":
                if(mc.thePlayer.isInWater()) {
                    if(mc.thePlayer.isCollidedHorizontally) {
                        mc.thePlayer.motionY = 0.22D;
                    } else {
                        mc.thePlayer.motionY = 0.13D;
                    }
                    mc.gameSettings.keyBindJump.pressed = false;
                }
                break;
            }
        }
    }

    @Listen
    public void onCollisionBoxes(CollisionBoxesEvent collisionBoxesEvent) {
        if(mc.thePlayer == null || mc.theWorld == null)
            return;
        
        switch(mode.getValue()) {
        case "Solid":
            BlockPos blockPos = collisionBoxesEvent.getBlockPos();

            if(mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.water) {
                collisionBoxesEvent.setBoundingBox(new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
            }
            break;
        }
    }

}
