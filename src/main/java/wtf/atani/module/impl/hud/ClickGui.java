package wtf.atani.module.impl.hud;

import org.lwjgl.input.Keyboard;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.screen.click.ClickGuiScreen;

@ModuleInfo(name = "ClickGui", description = "A clicky gui", category = Category.HUD, key = Keyboard.KEY_RSHIFT)
public class ClickGui extends Module {

    private ClickGuiScreen clickGuiScreen;

    @Override
    public void onEnable() {
        if(clickGuiScreen == null) {
            clickGuiScreen = new ClickGuiScreen();
        }
        mc.displayGuiScreen(clickGuiScreen);
        this.toggle();
    }

    @Override
    public void onDisable() {

    }

}
