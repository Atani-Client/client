package wtf.atani.module.impl.hud;

import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "CustomChat", description = "Improves the minecraft default chat.", category = Category.HUD)
public class CustomChat extends Module {

    // Hooked in GuiChat class & GuiNewChat class

    //public final CheckBoxValue noBackground = new CheckBoxValue("No Background", "Should the chat have no background?", this, false);
    public final StringBoxValue background = new StringBoxValue("Background", "Which background should the chat box use?", this, new String[] {"Normal", "TextWidth", "Off"});
    public final CheckBoxValue unlimitedChat = new CheckBoxValue("Unlimited Chat", "Should the module remove chat box character limit??", this, false);
    public final CheckBoxValue customFont = new CheckBoxValue("Custom Font", "Should the chat have a custom font?", this, false);

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}
