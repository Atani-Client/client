package tech.atani.client.feature.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.module.impl.movement.Speed;
import tech.atani.client.feature.module.storage.ModuleStorage;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;

@ModuleData(name = "InventoryMove", description = "Allows you to move in inventory", category = Category.PLAYER)
public class InventoryMove extends Module {
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[] {"Normal", "Intave"});
    private boolean sneaked;
    @Listen
    public void onMotionEvent(UpdateMotionEvent event) {
        if(Methods.mc.thePlayer == null || Methods.mc.theWorld == null)
            return;

        if(mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
            mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.keyCode);
            mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.keyCode);
            mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.keyCode);
            mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.keyCode);
            mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.keyCode);
            mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode);
            mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.keyCode);
            if(mode.is("Intave")) {
                sneaked = true;
                mc.gameSettings.keyBindJump.pressed = false;
                mc.thePlayer.setSneaking(true);
                mc.gameSettings.keyBindSneak.pressed = true;
            }
        } else if(mode.is("Intave") && sneaked) {
            sneaked = false;
            mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode);
            mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.keyCode);
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.keyCode);
        mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.keyCode);
    }

}