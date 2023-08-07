package wtf.atani.module.impl.hud;

import wtf.atani.event.events.Render2DEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.impl.hud.clientOverlay.IClientOverlayComponent;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.math.atomic.AtomicFloat;

import java.util.ArrayList;
import java.util.Comparator;

@ModuleInfo(name = "ClientOverlay", description = "A nice little overlay that shows you info about the client", category = Category.HUD)
public class ClientOverlay extends Module {

    private ArrayList<IClientOverlayComponent> clientOverlayComponents = new ArrayList<>();

    @Listen
    public void on2D(Render2DEvent render2DEvent) {
        AtomicFloat leftY = new AtomicFloat(0);
        AtomicFloat rightY = new AtomicFloat(0);
        if(clientOverlayComponents.isEmpty()) {
            clientOverlayComponents.add(ModuleStorage.getInstance().getByClass(ModuleList.class));
            clientOverlayComponents.add(ModuleStorage.getInstance().getByClass(WaterMark.class));
        }
        clientOverlayComponents.sort(Comparator.comparingInt(IClientOverlayComponent::getPriority));
        for(IClientOverlayComponent clientOverlayComponent : clientOverlayComponents) {
            clientOverlayComponent.draw(render2DEvent, leftY, rightY);
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
