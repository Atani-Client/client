package tech.atani.client.feature.module.impl.misc;

import com.google.common.base.Supplier;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import tech.atani.client.feature.module.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.feature.module.value.impl.CheckBoxValue;

@ModuleData(name = "Disabler", description = "Disable anti cheats", category = Category.MISCELLANEOUS)
public class Disabler extends Module {

	private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the disabler use?", this, new String[] {"Custom", "Verus Combat"});

	private final CheckBoxValue keepAlive = new CheckBoxValue("C00KeepAlive", "Should the module cancel C00KeepAlive?",
			this, false, new Supplier[]{() -> mode.is("Custom")});
	private final CheckBoxValue c0fConfirm = new CheckBoxValue("C0FConfirmTransaction", "Should the module cancel C0FConfirmTransaction?",
			this, false, new Supplier[]{() -> mode.is("Custom")});
	private final CheckBoxValue s2BChangeGameState = new CheckBoxValue("S2BChangeGameState", "Should the module cancel S2BChangeGameState?",
			this, false, new Supplier[]{() -> mode.is("Custom")});
	private final CheckBoxValue s07Respawn = new CheckBoxValue("S07Respawn", "Should the module cancel S07Respawn?",
			this, false, new Supplier[]{() -> mode.is("Custom")});
	private final CheckBoxValue c0bEntityAction = new CheckBoxValue("C0BEntityAction", "Should the module cancel C0BEntityAction?",
			this, false, new Supplier[]{() -> mode.is("Custom")});
	private final CheckBoxValue s05SpawnPosition = new CheckBoxValue("S05SpawnPosition", "Should the module cancel S05SpawnPosition?",
			this, false, new Supplier[]{() -> mode.is("Custom")});
	private final CheckBoxValue c0cInput = new CheckBoxValue("C0CInput", "Should the module cancel C0CInput?",
			this, false, new Supplier[]{() -> mode.is("Custom")});

	// Verus Combat
	private int verusCounter;

	@Listen
	public void onPacketEvent(PacketEvent event) {
		if (Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
			return;

		if (event.getType() == PacketEvent.Type.OUTGOING) {
			Packet<?> packet = event.getPacket();
			switch(mode.getValue()) {
				case "Custom":
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
					break;
				case "Verus Combat":
					if(packet instanceof C0FPacketConfirmTransaction) {
						if(mc.thePlayer.isDead) {
							verusCounter = 0;
						}

						if(verusCounter != 0) {
							event.setCancelled(true);
						}

						verusCounter++;
					} else if(packet instanceof C0BPacketEntityAction) {
						event.setCancelled(true);
					}
					break;
			}
		}
	}

	@Override
	public void onEnable() {
		verusCounter = 0;
	}

	@Override
	public void onDisable() {
		verusCounter = 0;
	}

}