package tech.atani.client.feature.module.impl.movement;

import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.module.storage.ModuleStorage;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.time.TimeHelper;

@ModuleData(name = "Test", description = "idk this will prolly instaban (it will)", category = Category.MOVEMENT)
public class Test extends Module {
    private final SliderValue<Integer> delay = new SliderValue<Integer>("Sneak Delay", "How big will the delay be to sneak?", this, 30, 0, 300, 0);
    private final TimeHelper timer = new TimeHelper();
    private int x;
    private boolean y;
    private float z;
    @Listen
    public void onRotation(RotationEvent rotationEvent) {
    }

    @Listen
    public final void onMotion(UpdateMotionEvent updateMotionEvent) {
        if(timer.hasReached(1000, true))
            // (Important)
            mc.thePlayer.sendChatMessage("/report Axidagret");
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() { Methods.mc.gameSettings.keyBindSneak.pressed = false; }
}
