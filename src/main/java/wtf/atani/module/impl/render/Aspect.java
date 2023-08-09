package wtf.atani.module.impl.render;

import wtf.atani.event.events.PerspectiveEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "Aspect", description = "Simulate chaning your aspect ratio", category = Category.RENDER)
public class Aspect extends Module {

    private final SliderValue<Float> aspect = new SliderValue<>("Aspect", "What'll the aspect be?", this, 1.0f, 0.1f, 5.0f, 1);
    private final CheckBoxValue hands = new CheckBoxValue("Hands", "Affect Hands?", this, true);

    @Listen
    public void onPerspective(PerspectiveEvent perspectiveEvent) {
        if(!perspectiveEvent.isHand() || hands.getValue()) {
            perspectiveEvent.setAspect(aspect.getValue());
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
