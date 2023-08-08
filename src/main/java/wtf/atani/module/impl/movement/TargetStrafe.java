package wtf.atani.module.impl.movement;

import wtf.atani.event.events.MoveEntityEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.impl.combat.KillAura;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.player.MoveUtil;
import wtf.atani.utils.player.RotationUtil;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "TargetStrafe", description = "Strafe around entities", category = Category.MOVEMENT)
public class TargetStrafe extends Module {

    private final SliderValue<Float> strafeSize = new SliderValue<>("Size", "How far should the player strafe from the target?", this, 2.5F, 1F, 6F, 1);
    private final CheckBoxValue jumpOnly = new CheckBoxValue("Jump Only", "Should the module only strafe when the jump key is pressed?", this, false);
    private final CheckBoxValue controllable = new CheckBoxValue("Controllable", "Should the strafe change direction when left or right key is pressed?", this, true);
    private final CheckBoxValue voidCheck = new CheckBoxValue("Void Check", "Should the strafe change direction when void is below?", this, true);
    private final CheckBoxValue collideCheck = new CheckBoxValue("Collide Check", "Should the strafe change direction when colliding on a wall?", this, true);

    private int direction = 1;

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        if(KillAura.curEntity != null) {
            if(controllable.getValue()) {
                if(mc.gameSettings.keyBindLeft.isKeyDown()) {
                    direction = 1;
                }

                if(mc.gameSettings.keyBindRight.isKeyDown()) {
                    direction = -1;
                }
            }

            if (isVoidBelow(mc.thePlayer.getPosition()) && voidCheck.getValue()) {
                direction = -direction;
            }

            if (collideCheck.getValue() && mc.thePlayer.isCollidedHorizontally) {
                direction = -direction;
            }
        }
    }

    @Listen
    public void onMove(MoveEntityEvent moveEntityEvent) {
        KillAura killAura = ModuleStorage.getInstance().getModule("KillAura");

        if(killAura.isEnabled() && KillAura.curEntity != null) {
            if(jumpOnly.getValue() && !isKeyDown(mc.gameSettings.keyBindJump.getKeyCode())) {
                return;
            }
            float yaw = RotationUtil.getRotation(KillAura.curEntity, false, false, 0, 0, 0, 0, false, 180, 180, 180, 180, false, false)[0];

            MoveUtil.setMoveSpeed(MoveUtil.getSpeed(), yaw, direction, (mc.thePlayer.getDistanceToEntity(KillAura.curEntity) <= strafeSize.getValue() - 1) ? 0 : 1);
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}
