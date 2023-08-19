package tech.atani.client.feature.module.impl.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import tech.atani.client.listener.event.events.minecraft.network.PacketEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.feature.module.value.impl.CheckBoxValue;

@ModuleData(name = "Disabler", description = "Disable anti cheats", category = Category.MISCELLANEOUS)
public class Disabler extends Module {

	private final CheckBoxValue keepAlive = new CheckBoxValue("C00KeepAlive", "Should the module cancel C00KeepAlive?",
			this, false);
	private final CheckBoxValue c0fConfirm = new CheckBoxValue("C0FConfirmTransaction", "Should the module cancel C0FConfirmTransaction?",
			this, false);
	private final CheckBoxValue s2BChangeGameState = new CheckBoxValue("S2BChangeGameState", "Should the module cancel S2BChangeGameState?",
			this, false);
	private final CheckBoxValue s07Respawn = new CheckBoxValue("S07Respawn", "Should the module cancel S07Respawn?",
			this, false);
	private final CheckBoxValue c0bEntityAction = new CheckBoxValue("C0BEntityAction", "Should the module cancel C0BEntityAction?",
			this, false);
	private final CheckBoxValue s05SpawnPosition = new CheckBoxValue("S05SpawnPosition", "Should the module cancel S05SpawnPosition?",
			this, false);
	private final CheckBoxValue c0cInput = new CheckBoxValue("C0CInput", "Should the module cancel C0CInput?",
			this, false);

	@Listen
	public void onPacketEvent(PacketEvent event) {
		if (Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
			return;

		if (event.getType() == PacketEvent.Type.OUTGOING) {
			Packet<?> packet = event.getPacket();

			if (packet instanceof C00PacketKeepAlive && keepAlive.getValue()) {
				event.setCancelled(true);
			}

			if (packet instanceof C0FPacketConfirmTransaction && c0fConfirm.getValue()) {
				event.setCancelled(true);
			}

			if (packet instanceof S2BPacketChangeGameState && s2BChangeGameState.getValue()) {
				event.setCancelled(true);
			}

			if (packet instanceof S07PacketRespawn && s07Respawn.getValue()) {
				event.setCancelled(true);
			}

			if (packet instanceof C0BPacketEntityAction && c0bEntityAction.getValue()) {
				event.setCancelled(true);
			}

			if (packet instanceof S05PacketSpawnPosition && s05SpawnPosition.getValue()) {
				event.setCancelled(true);
			}

			if (packet instanceof C0CPacketInput && c0cInput.getValue()) {
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void onEnable() {}

	@Override
	public void onDisable() {}

}