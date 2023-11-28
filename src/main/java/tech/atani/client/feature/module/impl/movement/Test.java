package tech.atani.client.feature.module.impl.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.time.TimeHelper;
import tech.atani.client.utility.player.PlayerUtil;

@ModuleData(name = "Test", description = "Test module", category = Category.MOVEMENT)
public class Test extends Module {

    @Listen
    public final void onMotion(UpdateMotionEvent updateMotionEvent) {
        PlayerUtil.addChatMessgae("PITCH: " + mc.thePlayer.rotationPitch, true);
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() { Methods.mc.gameSettings.keyBindSneak.pressed = false; }
}
