package tech.atani.client.feature.module.impl.misc;

import cn.muyang.nativeobfuscator.Native;
import net.minecraft.network.Packet;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.network.PacketEvent;
import tech.atani.client.listener.event.minecraft.player.movement.UpdateEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.utility.interfaces.Methods;
import tech.atani.client.utility.math.time.TimeHelper;

import java.util.ArrayDeque;
import java.util.ArrayList;

@Native
@ModuleData(name = "StaffDetector", description = "Detects Staff", category = Category.MISCELLANEOUS)
public class StaffDetector extends Module {
    private final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the staffdetector use?", this, new String[] {"BMC"});
    //private ArrayList<String> bmcStaff = new ArrayList<String>("s", "a");
    boolean active = false;

    @Listen
    public void onUpdate(UpdateEvent updateEvent) {

    }

    @Listen
    public void onPacket(PacketEvent event) {

    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

}