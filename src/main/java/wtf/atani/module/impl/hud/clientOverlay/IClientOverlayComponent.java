package wtf.atani.module.impl.hud.clientOverlay;

import wtf.atani.event.events.Render2DEvent;
import wtf.atani.utils.math.atomic.AtomicFloat;

public interface IClientOverlayComponent {

    void draw(Render2DEvent render2DEvent, AtomicFloat leftY, AtomicFloat rightY);

    int getPriority();
}
