package tech.atani.client.feature.command.impl;

import tech.atani.client.feature.command.Command;
import tech.atani.client.feature.command.data.CommandInfo;
import tech.atani.client.files.impl.ModulesFile;

@CommandInfo(name = "value", description = "change values")
public class Config extends Command {

    @Override
    public boolean execute(String[] args) {
		String doThis = args[2];

		if(doThis != "save" && doThis != "load") {
			if(doThis == "load") {
				//ModulesFile.load()
			}
		}
		return false;
		}
    }

	/*
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
							sendMessage("Value set to §e§l" + args[2].toUpperCase());
						} else if (value instanceof MultiStringBoxValue) {
							MultiStringBoxValue multiStringBoxValue = (MultiStringBoxValue) value;
							multiStringBoxValue.toggle(args[2]);
							sendMessage(args[2].toUpperCase() + " in Value set to §e§l" + multiStringBoxValue.getValue().contains(args[2]));
						}
						break;
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
	 */