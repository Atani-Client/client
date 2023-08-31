package tech.atani.client.feature.module.impl.render;

import com.google.common.base.Supplier;
import net.minecraft.item.ItemSword;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;
import tech.atani.client.feature.module.impl.combat.KillAura;
import tech.atani.client.feature.module.storage.ModuleStorage;
import tech.atani.client.feature.value.impl.CheckBoxValue;
import tech.atani.client.feature.value.impl.StringBoxValue;

//Hooked in ItemRenderer class && Minecraft class
@ModuleData(name = "BlockAnimations", description = "Changes your item blocking animation", category = Category.RENDER)
public class BlockAnimations extends Module {

    public final StringBoxValue mode = new StringBoxValue("Style", "What animation should the sword use?", this, new String[]{"1.7", "Atani", "Exhibition", "Flux", "Swang", "Swong", "Remix", "Spin", "Warped", "Push", "Slide"});
    public final CheckBoxValue blockHit = new CheckBoxValue("Block Hit", "Should the sword allow block hitting?", this, true);
    public final CheckBoxValue fake = new CheckBoxValue("Fake", "Should the module fake block hitting?", this, false);
    public final CheckBoxValue killauraOnly = new CheckBoxValue("KillAura Only", "Fake block hitting only if killaura has a target?", this, true, new Supplier[]{() -> blockHit.getValue()});

    private KillAura killAura;

    public boolean shouldFake() {
        if(killAura == null)
            killAura = ModuleStorage.getInstance().getByClass(KillAura.class);
        if(fake.getValue()) {
            if(mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword))
                return false;
            if(killauraOnly.getValue())
                return killAura.curEntity != null;
            else
                return mc.thePlayer.swingProgress > 0;
        }
        return false;
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}