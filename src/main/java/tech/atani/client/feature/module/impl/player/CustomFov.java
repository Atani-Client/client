package tech.atani.client.feature.module.impl.player;

import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateMotionEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.time.TimeHelper;

@ModuleData(name = "CustomFov", description = "Change FOV to a custom value over 110.", category = Category.PLAYER)
public class CustomFov extends Module {
    private final SliderValue<Integer> fov = new SliderValue<Integer>("FOV", "What will the fov be changed to?", this, 130, 10, 200, 0);

    @Listen
    public final void onMotion(UpdateMotionEvent updateMotionEvent) {
        mc.gameSettings.fovSetting = fov.getValue();
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
