package tech.atani.client.feature.command.impl;

import java.util.Optional;

import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.storage.ModuleStorage;
import tech.atani.client.feature.module.value.Value;
import tech.atani.client.feature.module.value.impl.CheckBoxValue;
import tech.atani.client.feature.module.value.impl.SliderValue;
import tech.atani.client.feature.module.value.impl.StringBoxValue;
import tech.atani.client.feature.module.value.impl.bool.BooleanParser;
import tech.atani.client.feature.module.value.impl.slider.NumberParser;
import tech.atani.client.feature.module.value.storage.ValueStorage;
import tech.atani.client.feature.command.Command;
import tech.atani.client.feature.command.data.CommandInfo;

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