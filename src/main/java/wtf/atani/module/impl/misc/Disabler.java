package wtf.atani.module.impl.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;

@ModuleInfo(name = "Disabler", description = "Disable anti cheats", category = Category.MISCELLANEOUS)
public class Disabler extends Module {

	private final CheckBoxValue keepAlive = new CheckBoxValue("C00KeepAlive", "Should the module cancel C00KeepAlive?",
			this, false);

	@Listen
	public void onPacketEvent(PacketEvent event) {
		if (mc.thePlayer == null || mc.theWorld == null)
			return;
		if (event.getType() == PacketEvent.Type.OUTGOING) {
			Packet<?> packet = event.getPacket();

			if (event.getPacket() instanceof C00PacketKeepAlive && keepAlive.getValue()) {
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void onEnable() {
	}

	@Override
	public void onDisable() {
	}

}