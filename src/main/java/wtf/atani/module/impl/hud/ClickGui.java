package wtf.atani.module.impl.hud;

import com.google.common.base.Supplier;
import org.lwjgl.input.Keyboard;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.screen.click.golden.GoldenClickGuiScreen;
import wtf.atani.screen.click.simple.SimpleClickGuiScreen;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.StringBoxValue;

@ModuleInfo(name = "ClickGui", description = "A clicky gui", category = Category.HUD, key = Keyboard.KEY_RSHIFT)
public class ClickGui extends Module {

    public final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Simple", "Golden", "Augustus 2.6", "Ryu", "Icarus", "Fatality"});
    public final CheckBoxValue openingAnimation = new CheckBoxValue("Opening Animation", "Animate the opening and closing of the gui?", this, true);
    public final StringBoxValue dropdownAnimation = new StringBoxValue("Animation Mode", "How will the opening animation look like", this, new String[]{"Scale-In", "Frame Scale-In", "Left to Right", "Right to Left", "Up to Down", "Down to Up"}, new Supplier[]{() ->
            mode.getValue().equalsIgnoreCase("Simple") || // Dropdown guis go here
            mode.getValue().equalsIgnoreCase("Augustus 2.6") ||
            mode.getValue().equalsIgnoreCase("Ryu") ||
            mode.getValue().equalsIgnoreCase("Icarus")}).setIdName("Dropdown Animation Mode");
    public final StringBoxValue nonDropdownAnimation = new StringBoxValue("Animation Mode", "How will the opening animation look like", this, new String[]{"Left to Right", "Right to Left", "Up to Down", "Down to Up"}, new Supplier[]{() ->
            mode.getValue().equalsIgnoreCase("Golden") || // Non-Dropdown guis go here
                    mode.getValue().equalsIgnoreCase("Fatality")}).setIdName("Non-Dropdown Animation Mode");

    public static SimpleClickGuiScreen clickGuiScreenSimple;
    public static GoldenClickGuiScreen clickGuiScreenGolden;

    @Override
    public void onEnable() {
        switch (this.mode.getValue()) {
            case "Simple":
                if(clickGuiScreenSimple == null) {
                    clickGuiScreenSimple = new SimpleClickGuiScreen();
                }
                mc.displayGuiScreen(clickGuiScreenSimple);
                break;
            case "Golden":
                if(clickGuiScreenGolden == null) {
                    clickGuiScreenGolden = new GoldenClickGuiScreen();
                }
                mc.displayGuiScreen(clickGuiScreenGolden);
                break;
        }
        this.setEnabled(false);
    }

    @Override
    public void onDisable() {

    }

}
