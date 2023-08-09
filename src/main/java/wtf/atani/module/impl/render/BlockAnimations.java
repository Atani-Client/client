package wtf.atani.module.impl.render;

import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "BlockAnimations", description = "Changes your item blocking animation", category = Category.RENDER)
public class BlockAnimations extends Module {

    //Hooked in ItemRenderer class && Minecraft class

    public final StringBoxValue mode = new StringBoxValue("Style", "What animation should the sword use?", this, new String[]{"1.7", "Atani", "Exhibition"});
    public final CheckBoxValue blockHit = new CheckBoxValue("Block Hit", "Should the sword allow block hitting?", this, true);

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}