package wtf.atani.module.impl.render;

import com.google.common.base.Supplier;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import wtf.atani.event.events.Render2DEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.utils.combat.FightUtil;
import wtf.atani.utils.math.InterpolationUtil;
import wtf.atani.utils.render.RenderUtil;
import wtf.atani.utils.render.StencilUtil;
import wtf.atani.utils.render.color.ColorUtil;
import wtf.atani.utils.render.shader.shaders.GLSLShader;
import wtf.atani.utils.transform.Transormer;
import wtf.atani.value.impl.CheckBoxValue;
import wtf.atani.value.impl.SliderValue;
import wtf.atani.value.impl.StringBoxValue;

import java.awt.*;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

@ModuleInfo(name = "ESP", description = "Render little things around players", category = Category.RENDER)
public class ESP extends Module {

    public StringBoxValue mode = new StringBoxValue("Mode", "Which mode will the module use?", this, new String[]{"2D", "Stencil (Broken)"});
    public CheckBoxValue blur = new CheckBoxValue("Blur", "Blur?", this, true, new Supplier[]{() -> mode.getValue().equalsIgnoreCase("Stencil")});
    public CheckBoxValue rangeLimited = new CheckBoxValue("Range Limited", "Limit to a certain range?", this, false);
    public SliderValue<Float> range = new SliderValue<>("Range", "What range will be the maximum?", this, 100f, 0f, 1000f, 0);
    public CheckBoxValue players = new CheckBoxValue("Players", "Attack Players?", this, true);
    public CheckBoxValue animals = new CheckBoxValue("Animals", "Attack Animals", this, true);
    public CheckBoxValue monsters = new CheckBoxValue("Monsters", "Attack Monsters", this, true);
    public CheckBoxValue invisible = new CheckBoxValue("Invisibles", "Attack Invisibles?", this, true);
    public CheckBoxValue color = new CheckBoxValue("Color", "Color the esp?", this, true);
    private StringBoxValue customColorMode = new StringBoxValue("Color Mode", "How will the esp be colored?", this, new String[]{"Static", "Fade", "Gradient", "Rainbow", "Astolfo Sky"}, new Supplier[]{() -> color.getValue()});
    private SliderValue<Integer> red = new SliderValue<>("Red", "What'll be the red of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> color.getValue() && customColorMode.getValue().equalsIgnoreCase("Static") || customColorMode.getValue().equalsIgnoreCase("Random") || customColorMode.getValue().equalsIgnoreCase("Fade") || customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> green = new SliderValue<>("Green", "What'll be the green of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> color.getValue() && customColorMode.getValue().equalsIgnoreCase("Static") || customColorMode.getValue().equalsIgnoreCase("Random") || customColorMode.getValue().equalsIgnoreCase("Fade") || customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> blue = new SliderValue<>("Blue", "What'll be the blue of the color?", this, 255, 0, 255, 0, new Supplier[]{() -> color.getValue() && customColorMode.getValue().equalsIgnoreCase("Static") || customColorMode.getValue().equalsIgnoreCase("Random") || customColorMode.getValue().equalsIgnoreCase("Fade") || customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> red2 = new SliderValue<>("Second Red", "What'll be the red of the second color?", this, 255, 0, 255, 0, new Supplier[]{() -> color.getValue() && customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> green2 = new SliderValue<>("Second Green", "What'll be the green of the second color?", this, 255, 0, 255, 0, new Supplier[]{() -> color.getValue() && customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Integer> blue2 = new SliderValue<>("Second Blue", "What'll be the blue of the second color?", this, 255, 0, 255, 0, new Supplier[]{() -> color.getValue() && customColorMode.getValue().equalsIgnoreCase("Gradient")});
    private SliderValue<Float> darkFactor = new SliderValue<>("Dark Factor", "How much will the color be darkened?", this, 0.49F, 0F, 1F, 2, new Supplier[]{() -> color.getValue() && customColorMode.getValue().equalsIgnoreCase("Fade")});

    // 2D
    private final Frustum frustum = new Frustum();

    // Stencil
    private final Transormer transormer = new Transormer();
    private GLSLShader glslShader = new GLSLShader("/assets/minecraft/atani/shaders/galaxy.fsh");
    long init = System.currentTimeMillis();

    public void render(ScaledResolution scaledResolution) {
        GL11.glPushMatrix();
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();

        glslShader.drawShader((int) (scaledResolution.getScaledWidth() * 1.5f), scaledResolution.getScaledHeight(), (System.currentTimeMillis() - init) / 1000F);

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(-1f, -1f);
        GL11.glVertex2f(-1f, 1f);
        GL11.glVertex2f(1f, 1f);
        GL11.glVertex2f(1f, -1f);
        GL11.glEnd();
        GL20.glUseProgram(0);

        GlStateManager.color(1, 1, 1);
        GL11.glPopMatrix();
    }

    @Listen
    public void on2D(Render2DEvent render2DEvent) {
        switch (mode.getValue()) {
            case "2D":
                frustum.setPosition(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
                for (final Entity entity : getWorld().loadedEntityList) {
                    if (!frustum.isBoundingBoxInFrustum(entity.getEntityBoundingBox())) continue;
                    if (entity == getPlayer() && mc.gameSettings.thirdPersonView == 0) continue;
                    if (!FightUtil.isValidWithPlayer(entity, rangeLimited.getValue() ? range.getValue() : 1000000, invisible.getValue(), players.getValue(), animals.getValue(), monsters.getValue())) continue;
                    final double[] coords = new double[4];
                    InterpolationUtil.convertBox(coords, entity);
                    RenderUtil.drawLine(coords[0], coords[1], coords[2], coords[1], 2.0f, Color.BLACK.getRGB());
                    RenderUtil.drawLine(coords[0], coords[1], coords[0], coords[3], 2.0f, Color.BLACK.getRGB());
                    RenderUtil.drawLine(coords[0], coords[3], coords[2], coords[3], 2.0f, Color.BLACK.getRGB());
                    RenderUtil.drawLine(coords[2], coords[1], coords[2], coords[3], 2.0f, Color.BLACK.getRGB());

                    RenderUtil.drawLine(coords[0], coords[1], coords[2], coords[1], 1.0f, color.getValue() ? getColor(1) : Color.WHITE.getRGB());
                    RenderUtil.drawLine(coords[0], coords[1], coords[0], coords[3], 1.0f, color.getValue() ? getColor(2) : Color.WHITE.getRGB());
                    RenderUtil.drawLine(coords[0], coords[3], coords[2], coords[3], 1.0f, color.getValue() ? getColor(3) : Color.WHITE.getRGB());
                    RenderUtil.drawLine(coords[2], coords[1], coords[2], coords[3], 1.0f, color.getValue() ? getColor(4) : Color.WHITE.getRGB());

                    GlStateManager.resetColor();
                }
                break;
            case "Stencil (Broken)":
                GlStateManager.pushAttrib();
                GlStateManager.pushMatrix();
                final ScaledResolution resolution = new ScaledResolution(mc);
                mc.getRenderManager().setRenderOutlines(true);
                transormer.collect();
                final AtomicBoolean empty = new AtomicBoolean(true);

                getWorld().loadedEntityList.forEach(entity -> {
                    if (FightUtil.isValidWithPlayer(entity, rangeLimited.getValue() ? range.getValue() : 1000000, invisible.getValue(), players.getValue(), animals.getValue(), monsters.getValue())) {
                        mc.getRenderManager().renderEntityStatic(entity, mc.timer.renderPartialTicks, false);
                        empty.set(false);
                    }
                });

                if (!empty.get()) {
                    StencilUtil.init();
                    transormer.draw();
                    StencilUtil.readBuffer(1);
                    render(render2DEvent.getScaledResolution());
                    GlStateManager.enableLighting();
                    if(blur.getValue())
                        RenderUtil.drawAcrylicBlur();
                    StencilUtil.uninit();
                } else {
                    mc.getFramebuffer().bindFramebuffer(true);
                    mc.entityRenderer.setupOverlayRendering();
                }
                mc.getRenderManager().setRenderOutlines(false);
                transormer.release();
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
                RenderUtil.resetColor();
                break;
        }
    }

    final Calendar calendar = Calendar.getInstance();

    private int getColor(int counter) {
        int color = 0;
        switch (this.customColorMode.getValue()) {
            case "Static":
                color = new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
                break;
            case "Fade": {
                int firstColor = new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
                color = ColorUtil.fadeBetween(firstColor, ColorUtil.darken(firstColor, darkFactor.getValue()), counter * 150L);
                break;
            }
            case "Gradient": {
                int firstColor = new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB();
                int secondColor = new Color(red2.getValue(), green2.getValue(), blue2.getValue()).getRGB();
                color = ColorUtil.fadeBetween(firstColor, secondColor, counter * 150L);
                break;
            }
            case "Rainbow":
                color = ColorUtil.getRainbow(3000, (int) (counter * 150L));
                break;
            case "Astolfo Sky":
                color = ColorUtil.blendRainbowColours(counter * 150L);
                break;
        }
        if(calendar.get(Calendar.DAY_OF_MONTH) == 28 && calendar.get(Calendar.MONTH) == Calendar.OCTOBER) {
            color = ColorUtil.blendCzechiaColours(counter * 150L);
        }
        if(calendar.get(Calendar.DAY_OF_MONTH) == 3 && calendar.get(Calendar.MONTH) == Calendar.OCTOBER) {
            color = ColorUtil.blendGermanColours(counter * 150L);
        }
        return color;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
