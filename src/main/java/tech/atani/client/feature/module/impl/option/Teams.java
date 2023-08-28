package tech.atani.client.feature.module.impl.option;

import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.StringBoxValue;

@ModuleData(name = "Teams", description = "Friendly fire will not be tolerated", category = Category.OPTIONS)
public class Teams extends Module {
    public StringBoxValue teams = new StringBoxValue("Teams", "How will the client behave with teammates?", this, new String[]{"Armor Color"});

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}