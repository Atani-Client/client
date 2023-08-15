package wtf.atani.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import wtf.atani.event.events.CollisionBoxesEvent;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "Spider", description = "Climb up walls", category = Category.MOVEMENT)
public class Spider extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[] {"Jump", "Collision", "Vulcan"});
    private final CheckBoxValue jumpOnly = new CheckBoxValue("Jump Only", "Should the module only work when pressing the jump key?", this, false);

    @Override
    public String getSuffix() {
    	return mode.getValue();
    }

    @Listen
    public void onPacket(PacketEvent packetEvent) {
        if(canClimbWall() && mode.getValue().equals("Vulcan")) {
            if(packetEvent.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = (C03PacketPlayer) packetEvent.getPacket();

                if(mc.thePlayer.ticksExisted % 3 == 0) {
                    float yaw = MoveUtil.getDirection();
                    double random = (Math.random() * 0.03 + 0.16);

                    packet.setY(packet.getY() - 0.015);

                    float f = yaw * 0.017453292F;
                    packet.setX(packet.getX() + (MathHelper.sin(f) * random));
                    packet.setZ(packet.getZ() - (MathHelper.cos(f) * random));
                }

                if(mc.thePlayer.ticksExisted % 2 == 0) {
                    packet.setOnGround(true);
                }
            }
        }
    }

    @Listen
    public void onCollisionBoxes(CollisionBoxesEvent collisionBoxesEvent) {
        if(canClimbWall()) {
            switch(mode.getValue()) {
            case "Collision":
                if (mc.thePlayer.motionY > 0) {
                    return;
                }

                BlockPos blockPos = collisionBoxesEvent.getBlockPos();

                collisionBoxesEvent.setBoundingBox(new AxisAlignedBB(blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockPos.getX() + 1, 1, blockPos.getZ() + 1));
                break;
            }
        }
    }
    
    @Listen
    public void onMotion(UpdateMotionEvent updateMotionEvent) {
        if(updateMotionEvent.getType() == UpdateMotionEvent.Type.MID) {
            if(jumpOnly.getValue() && !isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                return;
            }

            if(this.canClimbWall()) {
                switch (mode.getValue()) {
                case "Jump":
                case "Vulcan":
                    mc.thePlayer.jump();
                    break;
                }
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    private boolean canClimbWall() {
        return mc.thePlayer != null && mc.thePlayer.isCollidedHorizontally && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && mc.thePlayer.fallDistance < 1.0F;
    }

}
