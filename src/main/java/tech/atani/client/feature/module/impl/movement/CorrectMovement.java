package tech.atani.client.feature.module.impl.movement;

import tech.atani.client.feature.anticheat.check.Check;
import tech.atani.client.feature.module.impl.player.ScaffoldWalk;
import tech.atani.client.feature.module.storage.ModuleStorage;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.StringBoxValue;
import tech.atani.client.listener.event.minecraft.game.RunTickEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.utility.player.PlayerHandler;

@ModuleData(name = "CorrectMovement", description = "Aligns your movement yaw properly", category = Category.MOVEMENT, alwaysRegistered = true)
public class CorrectMovement extends Module {

    public StringBoxValue mode = new StringBoxValue("Mode", "How will the module fix the movement?", this, new String[]{"Strict", "Silent", "Aggressive"});
    public CheckBoxValue ignoreScaffold = new CheckBoxValue("Ignore Scaffold", "Will CorrectMovement ignore scaffold?", this, false);

    @Listen
    public final void onTick(RunTickEvent runTickEvent) {
        if(ModuleStorage.getInstance().getModule("ScaffoldWalk").isEnabled() && ignoreScaffold.getValue())
            return;

        PlayerHandler.moveFix = this.isEnabled();
        if(this.isEnabled()) {
            switch (this.mode.getValue()) {
                case "Strict": {
                    PlayerHandler.currentMode = PlayerHandler.MoveFixMode.STRICT;
                    break;
                }
                case "Aggressive": {
                    PlayerHandler.currentMode = PlayerHandler.MoveFixMode.AGGRESSIVE;
                    break;
                }
                case "Silent": {
                    PlayerHandler.currentMode = PlayerHandler.MoveFixMode.SILENT;
                    break;
                }
            }
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}
