package tech.atani.gui.imgui;

import imgui.renderer.ImImpl;
import tech.atani.Client;
import tech.atani.module.Module;
import tech.atani.value.Value;
import tech.atani.value.impl.BooleanValue;
import tech.atani.value.impl.ModeValue;
import tech.atani.value.impl.NumberValue;
import imgui.ImGui;
import imgui.type.ImInt;
import net.minecraft.client.gui.GuiScreen;

public class ImGuiScreen extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ImImpl.render(io -> {
            ImGui.begin(Client.NAME + " v" + Client.VERSION);

            if (ImGui.beginTabBar("##category")) {
                for (Module.Category category : Module.Category.values()) {
                    if (ImGui.beginTabItem(category.getName())) {

                        for (Module m : Client.INSTANCE.getModuleManager().getModulesFromCategory(category)) {
                            if (ImGui.collapsingHeader(m.getName())) {
                                if (ImGui.checkbox("Enabled", m.isToggled())) {
                                    m.toggle();
                                }

                                if (!m.getValues().isEmpty())
                                    ImGui.separator();

                                for (Value v : m.getValues()) {
                                    if (v.isVisible()) {
                                        if (v instanceof BooleanValue) {
                                            if (ImGui.checkbox(v.getName(), ((BooleanValue) v).isToggled()))
                                                ((BooleanValue) v).setToggled(!((BooleanValue) v).isToggled());
                                        }

                                        if (v instanceof NumberValue) {
                                            if (ImGui.sliderFloat(v.getName(), ((NumberValue) v).getFlt(), (float) ((NumberValue) v).getMin(), (float) ((NumberValue) v).getMax())) {
                                                ((NumberValue) v).setValue(((NumberValue) v).getFlt()[0]);
                                            }

                                            ((NumberValue) v).flt[0] = (float) ((NumberValue) v).getValue();
                                        }

                                        if (v instanceof ModeValue) {
                                            ImInt imInt = new ImInt(((ModeValue) v).index2);

                                            if (ImGui.combo(v.getName(), imInt, ((ModeValue) v).getModes2())) {
                                                ((ModeValue) v).setMode(((ModeValue) v).getModes().get(imInt.get()));
                                            }

                                            ((ModeValue) v).index2 = ((ModeValue) v).index;
                                        }
                                    }
                                }
                            }
                        }

                        ImGui.endTabItem();
                    }
                }

                ImGui.endTabBar();
            }

            ImGui.end();
        });
    }

}
