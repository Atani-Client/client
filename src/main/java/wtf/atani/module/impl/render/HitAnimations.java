package wtf.atani.module.impl.render;

import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "HitAnimations", description = "Changes your item swinging animation", category = Category.RENDER)
public class HitAnimations extends Module {

    //Hooked in ItemRenderer class && EntityLivingBase class

    public final SliderValue<Float> swingSpeed = new SliderValue<>("Swing Speed", "How fast should the item swing?", this, 1.2f, 0.1f, 3.5f, 1);
    public final CheckBoxValue smoothSwing = new CheckBoxValue("Smooth Swing", "Should the item swing smoothly?", this, false);

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}