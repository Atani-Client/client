package tech.atani.client.feature.module.impl.hud;

import com.google.common.base.Supplier;
import org.lwjgl.input.Keyboard;
import tech.atani.client.feature.guis.screens.clickgui.atani.AtaniClickGuiScreen;
import tech.atani.client.feature.guis.screens.clickgui.simple.SimpleClickGuiScreen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.guis.screens.clickgui.astolfo.AstolfoClickGuiScreen;
import tech.atani.client.feature.guis.screens.clickgui.augustus.AugustusClickGuiScreen;
import tech.atani.client.feature.guis.screens.clickgui.golden.GoldenClickGuiScreen;
import tech.atani.client.feature.guis.screens.clickgui.icarus.IcarusClickGuiScreen;
import tech.atani.client.feature.guis.screens.clickgui.ryu.RyuClickGuiScreen;
import tech.atani.client.feature.guis.screens.clickgui.xave.XaveClickGuiScreen;
import tech.atani.client.feature.module.value.impl.CheckBoxValue;
import tech.atani.client.feature.module.value.impl.StringBoxValue;

@ModuleData(name = "ClickGui", description = "A clicky gui", category = Category.HUD, key = Keyboard.KEY_RSHIFT)
public class ClickGui extends Module {

    public final StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"Simple", "Atani", "Golden", "Augustus 2.6", "Xave", "Ryu", "Icarus", "Fatality", "Astolfo"});
    public final CheckBoxValue openingAnimation = new CheckBoxValue("Opening Animation", "Animate the opening and closing of the gui?", this, true);
    public final StringBoxValue dropdownAnimation = new StringBoxValue("Animation Mode", "How will the opening animation look like", this, new String[]{"Scale-In", "Frame Scale-In", "Left to Right", "Right to Left", "Up to Down", "Down to Up"}, new Supplier[]{() ->
            mode.getValue().equalsIgnoreCase("Simple") || // Dropdown guis go here
            mode.getValue().equalsIgnoreCase("Astolfo") ||
            mode.getValue().equalsIgnoreCase("Augustus 2.6") ||
            mode.getValue().equalsIgnoreCase("Xave") ||
            mode.getValue().equalsIgnoreCase("Ryu") ||
            mode.getValue().equalsIgnoreCase("Icarus")}).setIdName("Dropdown Animation Mode");
    public final StringBoxValue nonDropdownAnimation = new StringBoxValue("Animation Mode", "How will the opening animation look like", this, new String[]{"Left to Right", "Right to Left", "Up to Down", "Down to Up"}, new Supplier[]{() ->
            mode.getValue().equalsIgnoreCase("Golden") || // Non-Dropdown guis go here
                    mode.getValue().equalsIgnoreCase("Atani") ||
                    mode.getValue().equalsIgnoreCase("Fatality")}).setIdName("Non-Dropdown Animation Mode");

    public static SimpleClickGuiScreen clickGuiScreenSimple;
    public static AtaniClickGuiScreen clickGuiScreenAtani;
    public static GoldenClickGuiScreen clickGuiScreenGolden;
    public static AugustusClickGuiScreen clickGuiScreenAugustus;
    public static XaveClickGuiScreen clickGuiScreenXave;
    public static RyuClickGuiScreen clickGuiScreenRyu;
    public static IcarusClickGuiScreen clickGuiScreenIcarus;
    public static AstolfoClickGuiScreen clickGuiScreenAstolfo;

    @Override
    public void onEnable() {
        switch (this.mode.getValue()) {
            case "Atani":
                if(clickGuiScreenAtani == null) {
                    clickGuiScreenAtani = new AtaniClickGuiScreen();
                }
                mc.displayGuiScreen(clickGuiScreenAtani);
                break;
            case "Astolfo":
                if(clickGuiScreenAstolfo == null) {
                    clickGuiScreenAstolfo = new AstolfoClickGuiScreen();
                }
                mc.displayGuiScreen(clickGuiScreenAstolfo);
                break;
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
            case "Augustus 2.6":
                if(clickGuiScreenAugustus == null) {
                    clickGuiScreenAugustus = new AugustusClickGuiScreen();
                }
                mc.displayGuiScreen(clickGuiScreenAugustus);
                break;
            case "Xave":
                if(clickGuiScreenXave == null) {
                    clickGuiScreenXave = new XaveClickGuiScreen();
                }
                mc.displayGuiScreen(clickGuiScreenXave);
                break;
            case "Ryu":
                if(clickGuiScreenRyu == null) {
                    clickGuiScreenRyu = new RyuClickGuiScreen();
                }
                mc.displayGuiScreen(clickGuiScreenRyu);
                break;
            case "Icarus":
                if(clickGuiScreenIcarus == null) {
                    clickGuiScreenIcarus = new IcarusClickGuiScreen();
                }
                mc.displayGuiScreen(clickGuiScreenIcarus);
                break;
            case "Fatality": {
                this.mode.setValue("Golden");
                break;
            }
        }
        this.setEnabled(false);
    }

    @Override
    public void onDisable() {

    }

}
