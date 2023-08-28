package tech.atani.client.feature.module.impl.movement;

import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.utility.player.PlayerUtil;

//Hooked in EntityLivingBase class & EntityPlayerSP class
@ModuleData(name = "NoSlowDown", description = "Removes the blocking & eating slowdown", category = Category.MOVEMENT)
public class NoSlowDown extends Module {
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this,  new String[]{"Vanilla", "Spoof", "Old NCP", "WatchDog", "Old Intave", "Placement"});

    // Spoof
    private int spoofSlot;

    // WatchDog
    private int watchDogSlot;

    @Override
    public String getSuffix() {
    	return mode.getValue();
    }
    
    @Listen
    public void onMotionEvent(UpdateMotionEvent event) {
        if(event.getType() == UpdateMotionEvent.Type.MID) {
            switch(mode.getValue()) {
                case "Vanilla":
                    break;
                case "Spoof":
                    spoofSlot += 1;

                    if(1 > spoofSlot || spoofSlot > 8) {
                        spoofSlot = 1;
                    }

                    if(mc.thePlayer.inventory.currentItem == spoofSlot) {
                        spoofSlot += 1;
                    }

                    if(mc.thePlayer.isUsingItem()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(spoofSlot));
                    }
                    break;
                case "Old NCP":
                    if (mc.thePlayer.isBlocking()) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    }
                    break;
                case "WatchDog":
                    if(this.isMoving()) {
                        if (mc.thePlayer.isBlocking()) {
                            watchDogSlot += 1;

                            if (1 > watchDogSlot || watchDogSlot > 8) {
                                watchDogSlot = 1;
                            }

                            if (mc.thePlayer.inventory.currentItem == watchDogSlot) {
                                watchDogSlot += 1;
                            }

                            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(watchDogSlot));
                        }

                        if(mc.thePlayer.isUsingItem()) {
                            if(mc.thePlayer.ticksExisted % 2 + Math.round(Math.random()) == 0) {
                                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
                            }
                        }
                    }
                    break;
                case "Old Intave":
                    if (mc.thePlayer.isUsingItem()) {
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(PlayerUtil.getIndexOfItem() % 8 + 1));
                        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(PlayerUtil.getIndexOfItem()));
                    }
                    break;
                case "Placement":
                    if (mc.thePlayer.isUsingItem() && mc.thePlayer.getItemInUse().getItem() instanceof ItemSword) {
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                        this.sendPacketUnlogged(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    }
            }
        }

        if(event.getType() == UpdateMotionEvent.Type.POST) {
            switch(mode.getValue()) {
                case "Old NCP":
                    if (mc.thePlayer.isBlocking()) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    }
                    break;
                case "Old Intave":
                    if (mc.thePlayer.isUsingItem()) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(PlayerUtil.getItemStack()));
                    }
                    break;
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
