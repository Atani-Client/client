package wtf.atani.screen.main.atani.button;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.utils.animation.Direction;
import wtf.atani.utils.animation.impl.DecelerateAnimation;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.shader.advanced.render.ingame.RenderableShaders;
import wtf.atani.utils.render.shader.legacy.shaders.RoundedShader;

import java.awt.*;

public class AtaniButton extends GuiButton
{

    private DecelerateAnimation decelerateAnimation = new DecelerateAnimation(200, 1, Direction.BACKWARDS);

    public AtaniButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public AtaniButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    /**
     * Draws this button to the screen.
     */
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {
            if(RenderUtil.isHovered(mouseX, mouseY, xPosition, yPosition, width, height))
                this.decelerateAnimation.setDirection(Direction.FORWARDS);
            else
                this.decelerateAnimation.setDirection(Direction.BACKWARDS);
            RenderableShaders.render(true, true, () -> {
                RoundedShader.drawRound(this.xPosition, this.yPosition, this.width, this.height, 5, new Color(0, 0, 0, 205 + (int) (this.decelerateAnimation.getOutput() * 50)));
            });
            FontRenderer fontRenderer = FontStorage.getInstance().findFont("Roboto", 19);
            fontRenderer.drawTotalCenteredString(displayString, this.xPosition + this.width / 2, this.yPosition + this.height / 2, -1);
        }
    }

}
