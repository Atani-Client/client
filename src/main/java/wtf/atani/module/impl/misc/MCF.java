package wtf.atani.module.impl.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Mouse;

import wtf.atani.combat.CombatManager;
import wtf.atani.combat.interfaces.IgnoreList;
import wtf.atani.event.events.UpdateMotionEvent;
import wtf.atani.event.radbus.Listen;
import wtf.atani.module.Module;
import wtf.atani.module.data.ModuleInfo;
import wtf.atani.module.data.enums.Category;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "MCF", description = "Friend other players with a mouse click", category = Category.MISCELLANEOUS)
public class MCF extends Module implements IgnoreList {
    private final List<Entity> friends = new ArrayList<>();

    private boolean clicked;

    public MCF() {
        CombatManager.getInstance().addIgnoreList(this);
    }

    @Listen
    public void onUpdateMotion(UpdateMotionEvent updateMotionEvent) {
        if(Mouse.isButtonDown(2)) {
            if(mc.pointedEntity != null && !clicked && mc.pointedEntity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) mc.pointedEntity;

                String message;

                if(friends.contains(mc.pointedEntity)) {
                    friends.remove(player);
                    message = "§c" + player.getCommandSenderName() + " is no longer a friend!";
                } else {
                    friends.add(player);
                    message = "§a" + player.getCommandSenderName() + " is now a friend!";
                }

                this.sendMessage(message, true);
            }
            clicked = true;
        } else {
            clicked = false;
        }
    }

    @Override
    public void onEnable() {
        this.clicked = false;
    }

    @Override
    public void onDisable() {
        this.clicked = false;
    }

    @Override
    public List<Entity> getIgnored() {
        return this.friends;
    }
}
