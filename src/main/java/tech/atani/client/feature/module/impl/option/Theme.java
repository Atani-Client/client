package tech.atani.client.feature.module.impl.option;

import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.module.impl.hud.ClickGui;
import tech.atani.client.feature.module.impl.hud.ModuleList;
import tech.atani.client.feature.module.impl.hud.WaterMark;
import tech.atani.client.feature.module.storage.ModuleStorage;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.feature.value.interfaces.ValueChangeListener;

@ModuleData(name = "Theme", description = "Load theme presets", category = Category.OPTIONS, frozenState = true)
public class Theme extends Module {

    private WaterMark waterMark;
    private ModuleList moduleList;
    private ClickGui clickGui;

    public final StringBoxValue preset = new StringBoxValue("Preset", "Which preset to load", this, new String[]{"None", "Modern", "Simple", "Golden", "Augustus 2.6", "Xave", "Ryu", "Icarus", "Fatality"}, new ValueChangeListener[]{(stage, value, oldValue, newValue) -> {
        if(waterMark == null || moduleList == null || clickGui == null){
            waterMark = ModuleStorage.getInstance().getByClass(WaterMark.class);
            moduleList = ModuleStorage.getInstance().getByClass(ModuleList.class);
            clickGui = ModuleStorage.getInstance().getByClass(ClickGui.class);
        }
        waterMark.watermarkMode.setValue((String) newValue);
        moduleList.moduleListMode.setValue((String) newValue);
        clickGui.mode.setValue((String) newValue);
    }});

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
