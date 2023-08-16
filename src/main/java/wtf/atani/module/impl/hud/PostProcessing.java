package wtf.atani.module.impl.hud;

import com.google.common.base.Supplier;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;

@ModuleInfo(name = "PostProcessing", description = "Cool little shaders", category = Category.HUD)
public class PostProcessing extends Module {

    public CheckBoxValue bloom = new CheckBoxValue("Bloom", "Render bloom shaders?", this, true);
    public SliderValue<Integer> radius = new SliderValue<>("Radius", "With how much radius will the bloom render?", this, 10, 0, 20, 0, new Supplier[]{() -> bloom.getValue()});
    public CheckBoxValue blur = new CheckBoxValue("Blur", "Render blur shaders?", this, true);

    public PostProcessing() {
        this.setEnabled(true);
    }

    public static PostProcessing getInstance() {
        return ModuleStorage.getInstance().getByClass(PostProcessing.class);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
