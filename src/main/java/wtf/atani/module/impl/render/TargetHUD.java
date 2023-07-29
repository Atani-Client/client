package wtf.atani.module.impl.render;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import wtf.atani.event.events.Render2DEvent;
import wtf.atani.event.events.UpdateEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;
import wtf.atani.module.impl.combat.KillAura;
import wtf.atani.module.storage.ModuleStorage;
import wtf.atani.utils.combat.FightUtil;
import wtf.atani.utils.render.RoundedUtil;

import java.awt.*;

@ModuleInfo(name = "TargetHUD", description = "Draws a little box with the targets info", category = Category.RENDER)
public class TargetHUD extends Module {

    @Listen
    public void onRender2D(Render2DEvent render2DEvent) {
        if(ModuleStorage.getInstance().getByClass(KillAura.class).isEnabled() && KillAura.curEntity != null && FightUtil.getRange(KillAura.curEntity) <= ModuleStorage.getInstance().getByClass(KillAura.class).attackRange.getValue().floatValue() && KillAura.curEntity instanceof EntityPlayer) {
            EntityLivingBase target = KillAura.curEntity;

            float x = render2DEvent.getScaledResolution().getScaledWidth() / 2 + 10;
            float y = render2DEvent.getScaledResolution().getScaledHeight() / 2;

           RoundedUtil.drawRound(x, y, 120, 45, 6, new Color(0, 0, 0, 180));

           //getCommandSenderName() == getName()
           mc.fontRendererObj.drawStringWithShadow(target.getCommandSenderName(), x + 5, y + 5, new Color(255,255,255).getRGB());
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

}