package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.reflect.Reflector;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import wtf.atani.font.storage.FontStorage;
import wtf.atani.utils.interfaces.ClientInformationAccess;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.RoundedUtil;
import wtf.atani.utils.render.animation.Direction;
import wtf.atani.utils.render.animation.impl.DecelerateAnimation;
import wtf.atani.utils.render.shader.shaders.GLSLShader;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback, ClientInformationAccess
{

    private GLSLShader glslShader = new GLSLShader("/assets/minecraft/atani/shaders/shader.fsh");
    private final long timer;
    private String currentScreen = "ChangeLog";

    public GuiMainMenu() {
        this.timer = System.currentTimeMillis();
    }


    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.firstPageButtons.clear();
        this.secondPageButtons.clear();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (button.id == 5)
        {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }

        if (button.id == 1)
        {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if (button.id == 2)
        {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if (button.id == 14 )
        {

        }

        if (button.id == 4)
        {
            this.mc.shutdown();
        }

        if (button.id == 6 && Reflector.GuiModList_Constructor.exists())
        {
            this.mc.displayGuiScreen((GuiScreen)Reflector.newInstance(Reflector.GuiModList_Constructor, new Object[] {this}));
        }

        if (button.id == 11)
        {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }

        if (button.id == 12)
        {
            ISaveFormat isaveformat = this.mc.getSaveLoader();
            WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

            if (worldinfo != null)
            {
                GuiYesNo guiyesno = GuiSelectWorld.func_152129_a(this, worldinfo.getWorldName(), 12);
                this.mc.displayGuiScreen(guiyesno);
            }
        }
    }

    private ArrayList<MenuButton> firstPageButtons = new ArrayList<>();
    private ArrayList<MenuButton> secondPageButtons = new ArrayList<>();

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.drawRect(0, 0, this.width, this.height, new Color(16, 16, 16).getRGB());

        GL11.glPushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();

        glslShader.drawShader((int) (width * 1.5f), height, (System.currentTimeMillis() - timer) / 1000F);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-1f, -1f);
        GL11.glVertex2f(-1f, 1f);
        GL11.glVertex2f(1f, 1f);
        GL11.glVertex2f(1f, -1f);
        GL11.glEnd();
        GL20.glUseProgram(0);

        GlStateManager.color(1, 1, 1);
        GL11.glPopMatrix();

        FontRenderer bigger = FontStorage.getInstance().findFont("Roboto", 21);
        float fourthX = this.width / 4F;
        float sixthY = this.height / 6F;
        RoundedUtil.drawRoundOutline(fourthX - 7, sixthY * 0.5f, fourthX, sixthY * 3, 15, 3, new Color(16, 16, 16).brighter().brighter(), new Color(16, 16, 16).brighter());
        RoundedUtil.drawRoundOutline(fourthX * 2 + 7, sixthY * 0.5f, fourthX, sixthY * 3,15, 3, new Color(16, 16, 16).brighter().brighter(), new Color(16, 16, 16).brighter());
        first: {
            float rectX = fourthX - 7;
            float rectY = sixthY * 0.5f;
            float rectWidth = fourthX;
            float rectHeight = sixthY * 3;
            // Adding Buttons
            if(this.firstPageButtons.isEmpty()) {
                float buttonY = rectY + (sixthY / 3 * 2);
                ArrayList<String> buttons = new ArrayList<>();
                buttons.add("SinglePlayer");
                buttons.add("MultiPlayer");
                buttons.add("Options");
                buttons.add("Account Manager");
                buttons.add("Client Settings");
                buttons.add("License");
                for(String button : buttons) {
                    this.firstPageButtons.add(new MenuButton(button, rectX, buttonY, rectWidth, 15));
                    buttonY += 15;
                }
            }
            // Logo
            FontStorage.getInstance().findFont("Android 101", 100).drawTotalCenteredStringWithShadow(CLIENT_NAME.toLowerCase(), rectX + rectWidth / 2, rectY + sixthY / 3, -1);
            for(MenuButton menuButton : this.firstPageButtons) {
                menuButton.draw(mouseX, mouseY);
            }
            // Version
            bigger.drawCenteredString(String.format("Running version %s", VERSION), rectX + rectWidth / 2, rectY + rectHeight - 4 - 10, new Color(80, 80, 80).getRGB());
            bigger.drawCenteredString(String.format("%s Version", "Premium"), rectX + rectWidth / 2, rectY + rectHeight - 4 - bigger.FONT_HEIGHT - 2 - 10, new Color(80, 80, 80).getRGB());
        }
        second: {
            
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private class MenuButton {
        private final String name;
        private final float posX, posY, width, height;
        private final Runnable action;
        private DecelerateAnimation hoveringAnimation = new DecelerateAnimation(500, 1, Direction.BACKWARDS);

        public MenuButton(String name, float posX, float posY, float width, float height, Runnable action) {
            this.name = name;
            this.posX = posX;
            this.posY = posY;
            this.width = width;
            this.height = height;
            this.action = action;
        }

        public MenuButton(String name, float posX, float posY, float width, float height) {
            this(name, posX, posY, width, height, () -> {
                currentScreen = name;
                secondPageButtons.clear();
            });
        }

        public void draw(int mouseX, int mouseY) {
            if(RenderUtil.isHovered(mouseX, mouseY, posX, posY, width, height)) {
                this.hoveringAnimation.setDirection(Direction.FORWARDS);
            } else {
                this.hoveringAnimation.setDirection(Direction.BACKWARDS);
            }
            RenderUtil.drawRect(posX, posY, width, height, new Color(255, 255, 255, (int) (20 * hoveringAnimation.getOutput())).getRGB());
            FontRenderer normal = FontStorage.getInstance().findFont("Roboto", 19);
            normal.drawTotalCenteredStringWithShadow(name, posX + width / 2, posY + height / 2, -1);
        }

        public String getName() {
            return name;
        }

        public float getPosX() {
            return posX;
        }

        public float getPosY() {
            return posY;
        }

        public float getWidth() {
            return width;
        }

        public float getHeight() {
            return height;
        }

        public Runnable getAction() {
            return action;
        }
    }

}
