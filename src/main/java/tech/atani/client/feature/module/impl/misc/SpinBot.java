package tech.atani.client.feature.module.impl.misc;

import com.google.common.base.Supplier;
import de.gerrygames.viarewind.utils.ChatUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.math.MathUtil;
import tech.atani.client.utility.player.PlayerUtil;

@ModuleData(name = "SpinBot", description = "Spins", category = Category.MISCELLANEOUS)
public class SpinBot extends Module {
    private int yaw;
    private final CheckBoxValue sprint = new CheckBoxValue("Sprint", "Sprint while spinbotting?", this, false);
    private final StringBoxValue mode = new StringBoxValue("Mode", "At what mode will the spinbot rotate?", this, new String[]{"Normal", "Randomization", "Vulcan"});
    /*
    @Listen
    public void onPacket(PacketEvent event) {

    }
     */

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        mc.thePlayer.setSprinting(sprint.getValue());
        switch (mode.getValue()) {
            case "Normal":
                yaw += 1;
                break;
            case "Randomization":
                yaw += Math.random() * 4;
                break;
            case "Vulcan":
                yaw += (int) (mc.thePlayer.ticksExisted % Math.round(Math.random() * 20) == 0 ? -Math.random() * 5 : Math.random() * 4);
                break;
        }
        if(yaw >= 360)
            yaw = 0;

        PlayerUtil.addChatMessgae("S: " + yaw, true);

        //rotationEvent.setYaw(yaw);
    }

    @Listen
    public final void onUpdate(UpdateEvent updateEvent) {
        mc.thePlayer.setSprinting(sprint.getValue());
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}