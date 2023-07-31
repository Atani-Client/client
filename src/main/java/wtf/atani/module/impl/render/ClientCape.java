package wtf.atani.module.impl.render;

import net.minecraft.util.ResourceLocation;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "ClientCape", description = "Equips Atani's custom cape", category = Category.RENDER)
public class ClientCape extends Module {

    @Listen
    public void onUpdate(UpdateEvent event) {
        getPlayer().setLocationOfCape(new ResourceLocation("atani/capes/AtaniCape.png"));
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {
        if(mc.thePlayer == null) {
            return;
        }
        mc.thePlayer.setLocationOfCape(null);
    }

}