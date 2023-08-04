package wtf.atani.module.impl.render;

import net.minecraft.potion.Potion;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "AntiBlind", description = "Removes bad effects from the player.", category = Category.RENDER)
public class AntiBlind extends Module {

    @Listen
    public void onUpdateEvent(UpdateEvent updateEvent) {
        if (mc.thePlayer.getActivePotionEffect(Potion.blindness) != null) {
            mc.thePlayer.removePotionEffect(Potion.blindness.getId());
        }

        if (mc.thePlayer.getActivePotionEffect(Potion.confusion) != null) {
            mc.thePlayer.removePotionEffect(Potion.confusion.getId());
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}