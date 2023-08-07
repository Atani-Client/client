package wtf.atani.module.impl.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.math.time.TimeHelper;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;

import java.util.ArrayDeque;

@ModuleInfo(name = "Blink", description = "Blocks your packets for a time being", category = Category.MISCELLANEOUS, alwaysEnabled = true)
public class Blink extends Module {

    private final ArrayDeque<Packet> inPacketDeque = new ArrayDeque<>();
    private final ArrayDeque<Packet> outPacketDeque = new ArrayDeque<>();
    private final TimeHelper fakeLagTimer = new TimeHelper();

    private final CheckBoxValue incoming = new CheckBoxValue("Incoming", "Queue incoming packets?", this, false);
    private final CheckBoxValue burst = new CheckBoxValue("Burst", "Burst out packets after disabling?", this, true);
    private final CheckBoxValue pulse = new CheckBoxValue("Pulse", "Disable and enable the module every x amount of ms?", this, false);
    private final SliderValue<Long> pulseDelay = new SliderValue<>("Pulse Delay", "What'll be the delay between pulsing?", this, 150L, 50L, 5000L, 1);

    boolean active = false;

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {
        if(active && !this.isEnabled()) {
            if(burst.getValue()) {
                while (!outPacketDeque.isEmpty()) {
                    sendPacketUnlogged(outPacketDeque.poll());
                }
            } else {
                // To prevent server from tping back (this doesn't actually work lmfao)
                sendPacket(new C03PacketPlayer());
            }
            while (!inPacketDeque.isEmpty()) {
                mc.getNetHandler().addToReceiveQueue(inPacketDeque.poll());
            }
            outPacketDeque.clear();
            active = false;
        }
    }

    @Listen
    public void onPacket(PacketEvent event) {
        if (active && (event.getType() == PacketEvent.Type.OUTGOING || incoming.getValue())) {
            if(event.getType() == PacketEvent.Type.INCOMING) {
                inPacketDeque.add(event.getPacket());
            } else {
                outPacketDeque.add(event.getPacket());
            }
            if (pulse.getValue() && fakeLagTimer.hasReached(pulseDelay.getValue().longValue())) {
                while (!outPacketDeque.isEmpty()) {
                    sendPacketUnlogged(outPacketDeque.poll());
                }
                while (!inPacketDeque.isEmpty()) {
                    mc.getNetHandler().addToReceiveQueue(inPacketDeque.poll());
                }
                fakeLagTimer.reset();
            }
            event.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {
        active = true;
    }

    @Override
    public void onDisable() {
    }

}