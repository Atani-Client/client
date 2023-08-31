package tech.atani.client.feature.module.impl.movement;

import com.google.common.base.Supplier;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import tech.atani.client.feature.value.impl.MultiStringBoxValue;
import tech.atani.client.feature.value.impl.SliderValue;
import tech.atani.client.listener.event.minecraft.player.movement.NoSlowEvent;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.ModuleData;
import tech.atani.client.feature.module.data.enums.Category;

@ModuleData(name = "NoSlowDown", description = "Removes the blocking & eating slowdown", category = Category.MOVEMENT)
public class NoSlowDown extends Module {

    private final MultiStringBoxValue items = new MultiStringBoxValue("Items", "Should the module disable slowdown with these items?", this, new String[] {"Sword"}, new String[] {"Sword", "Food", "Bow"});
    private final SliderValue<Float> swordForward = new SliderValue<Float>("Sword Forward", "How high should the sword forward multiplier be?", this, 1f, 0f, 1f, 2, new Supplier[]{() -> items.get("Sword")}),
            swordStrafe = new SliderValue<Float>("Sword Strafe", "How high should the sword strafe multiplier be?", this, 1f, 0f, 1f, 2, new Supplier[]{() -> items.get("Sword")}),
            foodForward = new SliderValue<Float>("Food Forward", "How high should the food forward multiplier be?", this, 1f, 0f, 1f, 2, new Supplier[]{() -> items.get("Food")}),
            foodStrafe = new SliderValue<Float>("Food Strafe", "How high should the food strafe multiplier be?", this, 1f, 0f, 1f, 2, new Supplier[]{() -> items.get("Food")}),
            bowForward = new SliderValue<Float>("Bow Forward", "How high should the bow forward multiplier be?", this, 1f, 0f, 1f, 2, new Supplier[]{() -> items.get("Bow")}),
            bowStrafe = new SliderValue<Float>("Bow Strafe", "How high should the bow strafe multiplier be?", this, 1f, 0f, 1f, 2, new Supplier[]{() -> items.get("Bow")});

    @Listen
    public void onNoSlowEvent(NoSlowEvent event) {
        ItemStack currentItem = mc.thePlayer.getCurrentEquippedItem();
        if (currentItem == null || !mc.thePlayer.isUsingItem() || !isMoving()) {
            return;
        }

        if (items.get("Sword") && currentItem.getItem() instanceof ItemSword) {
            event.setSprint(true);
            event.setForward(swordForward.getValue());
            event.setStrafe(swordStrafe.getValue());
        }

        if (items.get("Food") && currentItem.getItem() instanceof ItemFood) {
            event.setSprint(true);
            event.setForward(foodForward.getValue());
            event.setStrafe(foodStrafe.getValue());
        }

        if (items.get("Bow") && currentItem.getItem() instanceof ItemBow) {
            event.setSprint(true);
            event.setForward(bowForward.getValue());
            event.setStrafe(bowStrafe.getValue());
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}
}
