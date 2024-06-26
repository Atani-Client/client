package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;
import tech.atani.client.listener.event.minecraft.input.MoveButtonEvent;

public class MovementInputFromOptions extends MovementInput
{
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn)
    {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState()
    {
        final MoveButtonEvent event = new MoveButtonEvent(new MoveButtonEvent.Button(this.gameSettings.keyBindLeft.isKeyDown(), 90), new MoveButtonEvent.Button(this.gameSettings.keyBindRight.isKeyDown(), -90), new MoveButtonEvent.Button(this.gameSettings.keyBindBack.isKeyDown(), 180), new MoveButtonEvent.Button(this.gameSettings.keyBindForward.isKeyDown(), 0), this.gameSettings.keyBindSneak.isKeyDown(), this.gameSettings.keyBindJump.isKeyDown());
        event.publishItself();
        if (event.isCancelled()) return;

        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;

        if (event.getForward().isButton()) {
            ++this.moveForward;
        }

        if (event.getBackward().isButton()) {
            --this.moveForward;
        }

        if (event.getLeft().isButton()) {
            ++this.moveStrafe;
        }

        if (event.getRight().isButton()) {
            --this.moveStrafe;
        }

        this.jump = event.isJump();
        this.sneak = event.isSneak();

        if (this.sneak) {
            this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
            this.moveForward = (float) ((double) this.moveForward * 0.3D);
        }
    }
}
