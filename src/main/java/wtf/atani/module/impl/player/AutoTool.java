package wtf.atani.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

@ModuleInfo(name = "AutoTool", description = "Automatically equips the best tool for the job.", category = Category.PLAYER)
public class AutoTool extends Module {

    @Listen
    public void onMotionEvent(UpdateMotionEvent event) {
        if(event.getType() == UpdateMotionEvent.Type.MID) {
            if (!mc.gameSettings.keyBindAttack.isKeyDown())
                return;

            BlockPos position = mc.objectMouseOver.getBlockPos();
            if(position == null)
                return;

            Block block = mc.theWorld.getBlockState(position).getBlock();
            if(block == null)
                return;

            float best = 1F;
            int slot = 1000;

            for (int index = 0; index < 9; index += 1) {
                ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(index);

                if (itemStack == null)
                    continue;

                float speed = itemStack.getStrVsBlock(block);

                if (speed > best) {
                    best = speed;
                    slot = index;
                }
            }

            if (slot == 1000)
                return;

            mc.thePlayer.inventory.currentItem = slot;
        }
    }

    @Override
    public void onEnable() {}

    @Override
    public void onDisable() {}

}