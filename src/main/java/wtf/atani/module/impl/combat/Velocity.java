package wtf.atani.module.impl.combat;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.events.SilentMoveEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "Velocity", description = "Modifies your velocity", category = Category.COMBAT)
public class Velocity extends Module {

    public StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[] {"Simple", "Intave", "Old Grim", "Vulcan", "AAC v4", "AAC v5 Packet"});
    public SliderValue<Integer> horizontal = new SliderValue<>("Horizontal %", "How much horizontal velocity will you take?", this, 100, 0, 100, 0);
    public SliderValue<Integer> vertical = new SliderValue<>("Vertical %", "How much vertical velocity will you take?", this, 100, 0, 100, 0);
    public SliderValue<Float> aacv4Reduce = new SliderValue<>("Reduce", "How much motion will be reduced?", this, 0.62F,0F,1F, 1);

    private KillAura killAura;
    private int counter;

    int grimCancel = 0;
    int updates = 0;

    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        switch (mode.getValue()) {
            case "AAC v4":
                if (mc.thePlayer.hurtTime > 0 && !mc.thePlayer.onGround){
                    mc.thePlayer.motionX *= aacv4Reduce.getValue().floatValue();
                    mc.thePlayer.motionZ *= aacv4Reduce.getValue().floatValue();
                }
                break;
            case "Old Grim":
                updates++;

                if (updates >= 0 || updates >= 10) {
                    updates = 0;
                    if (grimCancel > 0){
                        grimCancel--;
                    }
                }
                break;
        }
    }

    @Listen
    public final void onPacket(PacketEvent packetEvent) {
        switch (mode.getValue()) {
            case "Vulcan":
                if (mc.thePlayer.hurtTime > 0 && packetEvent.getPacket() instanceof C0FPacketConfirmTransaction) {
                    packetEvent.setCancelled(true);
                }
                if(packetEvent.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) packetEvent.getPacket();
                    if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                        packetEvent.setCancelled(true);
                    }
                }
                break;
            case "Simple":
                if(packetEvent.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) packetEvent.getPacket();
                    if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                        if(horizontal.getValue() == 0 && vertical.getValue() == 0)
                            packetEvent.setCancelled(true);
                        packet.setMotionX((int) (packet.getMotionX() * (horizontal.getValue().doubleValue() / 100D)));
                        packet.setMotionY((int) (packet.getMotionY() * (vertical.getValue().doubleValue() / 100D)));
                        packet.setMotionZ((int) (packet.getMotionZ() * (horizontal.getValue().doubleValue() / 100D)));
                    }
                }
                break;
            case "Old Grim":
                if(packetEvent.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) packetEvent.getPacket();
                    if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                        packetEvent.setCancelled(true);
                        grimCancel = 6;
                    }
                }
                if(packetEvent.getPacket() instanceof S32PacketConfirmTransaction && grimCancel > 0) {
                    packetEvent.setCancelled(true);
                    grimCancel--;
                }
                break;
            case "AAC v5 Packet": {
                if(packetEvent.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) packetEvent.getPacket();
                    if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                        sendPacketUnlogged(
                                new C03PacketPlayer.C04PacketPlayerPosition(
                                        mc.thePlayer.posX,
                                        Double.MAX_VALUE,
                                        mc.thePlayer.posZ,
                                        mc.thePlayer.onGround
                                )
                        );
                        packetEvent.setCancelled(true);
                    }
                }
            }
        }
    }

    @Listen
    public final void onSilent(SilentMoveEvent silentMoveEvent) {
        switch(this.mode.getValue()) {
            case "Intave":
                if (Velocity.mc.thePlayer.hurtTime == 9 && Velocity.mc.thePlayer.onGround && this.counter++ % 2 == 0) {
                    Velocity.mc.thePlayer.movementInput.jump = true;
                    break;
                }
        }
    }

    @Override
    public void onEnable() {
        this.killAura = ModuleStorage.getInstance().getByClass(KillAura.class);
        grimCancel = 0;
    }

    @Override
    public void onDisable() {

    }
}
