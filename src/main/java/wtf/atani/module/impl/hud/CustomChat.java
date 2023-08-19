package wtf.atani.module.impl.hud;

import com.google.common.base.Supplier;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.StringBoxValue;

// Hooked in GuiChat class & GuiNewChat class
@ModuleInfo(name = "CustomChat", description = "Improves the minecraft default chat.", category = Category.HUD)
public class CustomChat extends Module {

    public final StringBoxValue background = new StringBoxValue("Background", "Which background should the chat box use?", this, new String[] {"Normal", "Adaptive", "Off"});
    public final CheckBoxValue unlimitedChat = new CheckBoxValue("Unlimited Chat", "Should the module remove chat box character limit??", this, false);
    public final CheckBoxValue customFont = new CheckBoxValue("Custom Font", "Should the chat have a custom font?", this, false);
    public final CheckBoxValue backgroundBlur = new CheckBoxValue("Blur Background", "Should the chat box have a blur?", this, false, new Supplier[]{() -> (background.is("Normal") || background.is("Adaptive"))});

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}
