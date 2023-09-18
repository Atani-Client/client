package tech.atani.client.feature.module.impl.combat;

import com.google.common.base.Supplier;
import com.viaversion.viabackwards.utils.ChatUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.module.storage.ModuleStorage;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.player.rotation.RayTraceRangeEvent;
import tech.atani.client.listener.event.minecraft.player.rotation.RotationEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.player.combat.FightUtil;
import tech.atani.client.utility.player.rotation.RotationUtil;

import java.util.Arrays;
import java.util.List;

@ModuleData(name = "HitBoxes", description = "Allows you to hit further", category = Category.COMBAT)
public class HitBoxes extends Module {

    public StringBoxValue hitBoxMode = new StringBoxValue("HitBox Mode", "Which mode should hitbox use?", this, new String[] {"Normal", "Legit"});

    @Listen
    public final void onRotation(RotationEvent rotationEvent) {
        if(hitBoxMode.is("Legit")) {
            //fix range!
            // Killaura, gotta make with these List<EntityLivingBase> targets = FightUtil.getMultipleTargets(findRange.getValue(), players.getValue(), animals.getValue(), walls.getValue(), monsters.getValue(), invisible.getValue());
            List<EntityLivingBase> targets = FightUtil.getMultipleTargets(3.0, true, false, false, true, true);
            
            final float[] rotations = RotationUtil.getRotation(targets.get(1), "Bruteforce", 0, true, false, 0, 0, 0, 0, true, 0, 0, 0, 0, false, false);

            if(mc.gameSettings.keyBindAttack.pressed)
                mc.thePlayer.sendChatMessage("rots: " + Arrays.toString(rotations));
        }
    }
    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
