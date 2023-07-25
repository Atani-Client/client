package wtf.atani.module.impl.combat;

import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "KillAura", description = "Attacks people", category = Category.COMBAT)
public class KillAura extends Module {

    CheckBoxValue checkBoxValue = new CheckBoxValue("CheckBox", "Checky Box", this, true);
    SliderValue<Integer> sliderValue = new SliderValue<>("Slider", "Slidery", this, 50, 0, 100, 0);
    SliderValue<Float> sliderValueFloat = new SliderValue<>("Slider Float", "Slidery", this, 50f, 0f, 100f, 1);
    SliderValue<Double> sliderValueDouble = new SliderValue<>("Slider Double", "Slidery", this, 50d, 0d, 100d, 2);

    StringBoxValue stringBoxValue = new StringBoxValue("String Box", "something", this, new String[]{"xd", "Stuff"});

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}
