package wtf.atani.module.impl.misc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "ClientSpoofer", description = "Make servers think you are using a different client", category = Category.MISCELLANEOUS)
public class ClientSpoofer extends Module {

    private final StringBoxValue mode = new StringBoxValue("Mode", "What client should we spoof as?", this, new String[] {"Lunar", "Forge"});

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

    @Listen
    public void onPacket(PacketEvent packetEvent) {
        if(packetEvent.getPacket() instanceof C17PacketCustomPayload) {
            C17PacketCustomPayload packet = (C17PacketCustomPayload) packetEvent.getPacket();

            String data = "";
            boolean wrapped = true;

            switch(mode.getValue()) {
            case "Lunar":
                data = "Lunar";
                break;
            case "Forge":
                data = "FML";
                break;
            }

            ByteBuf byteBuf = wrapped ? Unpooled.wrappedBuffer(data.getBytes()) : Unpooled.buffer();
            PacketBuffer buffer = wrapped ? new PacketBuffer(Unpooled.wrappedBuffer(byteBuf)) : new PacketBuffer(byteBuf).writeString(data);

            packet.setData(buffer);
        }
    }

}
