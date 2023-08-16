package wtf.atani.module.impl.render;

import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.SliderValue;

//Hooked in ItemRenderer class
@ModuleInfo(name = "ViewModel", description = "Changes your hand item size & position", category = Category.RENDER)
public class ViewModel extends Module {

    public final SliderValue<Float> xPos = new SliderValue<>("X Position", "Where should the item go on the X axis?", this, 0.56f, 0.10f, 1f, 2);
    public final SliderValue<Float> yPos = new SliderValue<>("Y Position", "Where should the item go on the Y axis?", this, 0.52f, 0.10f, 1f, 2);
    public final SliderValue<Float> scale = new SliderValue<>("Item Scale", "What size do you want the item to be?", this, 0.4f, 0.01f, 1f, 2);

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}