package tech.atani.client.feature.module.impl.combat;

import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.listener.event.minecraft.game.RunTickEvent;
import tech.atani.client.listener.radbus.Listen;

@ModuleData(name = "OtherAura", description = "Automatically attacks entities for you.", category = Category.COMBAT)
public class OtherAura extends Module {

}
