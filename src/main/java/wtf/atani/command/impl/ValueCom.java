package wtf.atani.command.impl;

import java.util.Optional;

import org.lwjgl.input.Keyboard;

import wtf.atani.command.Command;
import wtf.atani.command.data.CommandInfo;
import wtf.atani.module.Module;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.value.Value;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;
import wtf.atani.value.impl.bool.BooleanParser;
import wtf.atani.value.impl.slider.NumberParser;
import wtf.atani.value.storage.ValueStorage;

@CommandInfo(name = "value", description = "change values")
public class ValueCom extends Command {

    @Override
    public boolean execute(String[] args) {
        if(args.length == 3) {
            final Module module = ModuleStorage.getInstance().getModule(args[0]);
            if(module != null) {
            	String valueName = args[1];
        		boolean found = false;
            	for(Value value : ValueStorage.getInstance().getValues(module)) {
            		if(value.getName().replace(" ", "").equalsIgnoreCase(valueName) ) {
            			found = true;
            			if(value instanceof CheckBoxValue) {
            				CheckBoxValue checkBoxValue = (CheckBoxValue) value;
            				Optional<Boolean> optional = BooleanParser.parse(args[2]);
            				if(optional != null) {
                				checkBoxValue.setValue(optional.get());
                                sendMessage("Value set to §e§l" + args[2].toUpperCase());
            				} else {
            	                sendError("COULD NOT PARSE", "§aParsing §l" + args[2] + " §afailed!");
            				}
            			} else if(value instanceof SliderValue) {
            				SliderValue sliderValue = (SliderValue) value;
            				Number result = NumberParser.parse(args[2], sliderValue.getValue().getClass());
            				if(result != null) {
            					sliderValue.setValue(result);
                                sendMessage("Value set to §e§l" + args[2].toUpperCase());
            				} else {
            	                sendError("COULD NOT PARSE", "§aParsing §l" + args[2] + " §afailed!");
            				}
            			} else if (value instanceof StringBoxValue) {
							StringBoxValue stringBoxValue = (StringBoxValue) value;
							stringBoxValue.setValue(args[2]);
						}
            		}
            	}
        		if(!found) {
                    sendError("DOES NOT EXIST", "§aValue §l" + args[1] + " §anot found!");
        		}
            }else{
                sendError("DOES NOT EXIST", "§aModule §l" + args[0] + " §anot found!");
            }
        } else if (args.length == 0) {
            sendHelp(this, "[Module] [Value Name] [New Value]");
        } else {
            return false;
        }
        return true;
    }
}