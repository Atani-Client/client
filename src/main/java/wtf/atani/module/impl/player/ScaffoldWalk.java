package wtf.atani.module.impl.player;

import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import wtf.atani.event.events.ClickingEvent;
import wtf.atani.event.events.RotationEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;

import java.util.concurrent.ThreadLocalRandom;

@ModuleInfo(name = "ScaffoldWalk", description = "Bridging automatically", category = Category.PLAYER)
public class ScaffoldWalk extends Module {

    private final CheckBoxValue swinging = new CheckBoxValue("Swinging", "Swing when placing blocks?", this, true);
    private final CheckBoxValue sprint = new CheckBoxValue("Sprint", "Allow sprinting?", this, false);
    private final CheckBoxValue reverseMovement = new CheckBoxValue("Reverse Movement", "Reverse your movement?", this, false);
    private final CheckBoxValue randomizePitch = new CheckBoxValue("Randomize Pitch", "Randomize pitch rotation?", this, false);
    private final SliderValue<Long> delay = new SliderValue<>("Delay", "What will be the delay between placing?", this, 0L, 0L, 1000L, 0);
    private final SliderValue<Long> unSneakDelay = new SliderValue<>("Unsneak delay", "What will be the delay between unsneaking?", this, 0L, 0L, 1000L, 0);

    private final TimeHelper timeHelper = new TimeHelper(), unsneakTimeHelper = new TimeHelper();

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        rotationEvent.setPitch(randomizePitch.getValue() ? (float) ThreadLocalRandom.current().nextDouble(83, 83.3) : 83);
        rotationEvent.setYaw(mc.thePlayer.rotationYaw + 180);
    }

    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        getPlayer().setSprinting(sprint.getValue());
        getGameSettings().keyBindSprint.pressed = sprint.getValue();

        if (reverseMovement.getValue()) {
            getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
            getGameSettings().keyBindForward.pressed = false;
        }

        if (unSneakDelay.getValue() == 0 || unsneakTimeHelper.hasReached((long) (unSneakDelay.getValue()))) {
            getGameSettings().keyBindSneak.pressed = false;
        }
        for (int y = -1; y < 0; y++) {
            final Vec3 pos = getPlayer().getPositionVector().addVector(0, y, 0);
            final BlockPos blockPos = new BlockPos(pos);
            if (getWorld().isAirBlock(blockPos)) {
                getGameSettings().keyBindSneak.pressed = true;
                unsneakTimeHelper.reset();
            }
        }
    }

    ;

    @Listen
    public void onClicking(ClickingEvent clickingEvent) {
        if (delay.getValue() == 0 || timeHelper.hasReached((long) (delay.getValue()))) {
            final ItemStack itemstack = getPlayer().getHeldItem();
            if (itemstack != null && itemstack.getItem() instanceof ItemBlock) {
                if (mc.objectMouseOver != null) {
                    final BlockPos blockpos = mc.objectMouseOver.getBlockPos();
                    if (mc.theWorld.getBlockState(blockpos).getBlock().getMaterial() != Material.air) {
                        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), blockpos, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec)) {
                            if (this.swinging.getValue())
                                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                        }
                        this.timeHelper.reset();
                    }
                }
            }
        }
    }

    private boolean canTower() {
        return getGameSettings().keyBindJump.pressed;
    }

    @Override
    public void onEnable() {
        timeHelper.reset();
    }

    @Override
    public void onDisable() {
        getGameSettings().keyBindSneak.pressed = isKeyDown(getGameSettings().keyBindSneak.getKeyCode());
        getGameSettings().keyBindBack.pressed = isKeyDown(getGameSettings().keyBindBack.getKeyCode());
        getGameSettings().keyBindForward.pressed = isKeyDown(getGameSettings().keyBindForward.getKeyCode());
    }
}
