package wtf.atani.module.impl.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import org.lwjgl.input.Keyboard;
import wtf.atani.event.events.PacketEvent;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;

@ModuleInfo(name = "InventoryMove", description = "Move inside your inventory.", category = Category.MOVEMENT)
public class InventoryMove extends Module {

    private final CheckBoxValue openPacket = new CheckBoxValue("No Open Packet", "Should the module send open packets?", this, false);

    @Listen
    public final void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {
        if (updateMotionEvent.getType() == UpdateMotionEvent.Type.PRE) {
            block3 : {
                KeyBinding[] moveKeys;
                block2 : {
                    moveKeys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSneak, mc.gameSettings.keyBindSprint};
                    if (mc.currentScreen == null || (mc.currentScreen instanceof GuiChat))
                        break block2;
                    for (KeyBinding key : moveKeys) {
                        key.pressed = Keyboard.isKeyDown(key.getKeyCode());
                    }
                    break block3;
                }

                for (KeyBinding bind : moveKeys) {
                    if (Keyboard.isKeyDown(bind.getKeyCode()))
                        continue;
                    KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                }
            }
        }
    }

    @Listen
    public final void onPacketEvent(PacketEvent packetEvent) {
        if (packetEvent.getType() == PacketEvent.Type.OUTGOING && openPacket.getValue()
                && (packetEvent.getPacket() instanceof S2DPacketOpenWindow || packetEvent.getPacket() instanceof S2EPacketCloseWindow)) {
            packetEvent.setCancelled(true);
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}
