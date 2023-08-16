package wtf.atani.module.impl.render;

import net.minecraft.util.ResourceLocation;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "ClientCape", description = "Equips Atani's custom cape", category = Category.RENDER)
public class ClientCape extends Module {

    public final StringBoxValue cape = new StringBoxValue("Cape", "Which cape to use?", this, new String[]{"Normal", "Small Text", "Small Text Up", "Atan+I Logo", "Minecraft Res"});

    ResourceLocation normal = new ResourceLocation("atani/capes/AtaniCape.png");
    ResourceLocation small = new ResourceLocation("atani/capes/AtaniCapeSmall.png");
    ResourceLocation smallUp = new ResourceLocation("atani/capes/AtaniCapeSmallUp.png");
    ResourceLocation ataniFunctionLogo = new ResourceLocation("atani/capes/AtaniFunctionLogoCape.png");
    ResourceLocation pixelated = new ResourceLocation("atani/capes/AtaniCapePixelated.png");

    @Listen
    public void onUpdate(UpdateEvent event) {
        ResourceLocation resourceLocation = null;
        switch (cape.getValue()) {
            case "Normal":
                resourceLocation = normal;
                break;
            case "Small Text":
                resourceLocation = small;
                break;
            case "Small Text Up":
                resourceLocation = smallUp;
                break;
            case "Atan+I Logo":
                resourceLocation = ataniFunctionLogo;
                break;
            case "Minecraft Res":
                resourceLocation = pixelated;
                break;
        }
        getPlayer().setLocationOfCape(resourceLocation);
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