package tech.atani.client.feature.module.impl.misc;

import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.value.impl.MultiStringBoxValue;

@ModuleData(name = "Test", description = "test", category = Category.MISCELLANEOUS)
public class Test extends Module {

    public MultiStringBoxValue multi = new MultiStringBoxValue("Multi", this, new String[] {"one"}, new String[] {"one", "two", "three", "four", "five"});

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
