package tech.atani.client.feature.module.storage;

import de.florianmichael.rclasses.storage.Storage;
import tech.atani.client.feature.module.impl.chat.*;
import tech.atani.client.feature.module.impl.combat.*;
import tech.atani.client.feature.module.impl.hud.*;
import tech.atani.client.feature.module.impl.misc.*;
import tech.atani.client.feature.module.impl.movement.*;
import tech.atani.client.feature.module.impl.option.*;
import tech.atani.client.feature.module.impl.player.*;
import tech.atani.client.feature.module.impl.render.*;
import tech.atani.client.feature.module.impl.server.hypixel.*;
import tech.atani.client.feature.module.impl.server.loyisa.AutoHeal;
import tech.atani.client.feature.module.impl.server.qplay.AutoPig;
import tech.atani.client.listener.event.minecraft.input.KeyInputEvent;
import tech.atani.client.listener.handling.EventHandling;
import tech.atani.client.listener.radbus.Listen;
import tech.atani.client.feature.module.Module;
import tech.atani.client.feature.module.data.enums.Category;

import java.util.ArrayList;

public class ModuleStorage extends Storage<Module> {

    private static ModuleStorage instance;

    @Override
    public void init() {
        EventHandling.getInstance().registerListener(this);
        this.add(
                //Chat
                new AdBlocker(),
                new AntiAntiSwear(),
                new AntiDumbMessages(),
                new AutoGG(),
                new AutoGL(),
                new AutoAuth(),
                new KillInsult(),
                new QuickMathSolver(),
                //Combat
                new AntiBot(),
                new AntiFireball(),
                new Backtrack(),
                new Criticals(),
                new Extinguish(),
                new HitBoxes(),
                new KeepKB(),
                new KillAura(),
                new NoClickDelay(),
                new Reach(),
                new TickBase(),
                new TriggerBot(),
                new Velocity(),
                //HUD
                new ClickGui(),
                new ClientOverlay(),
                new CustomChat(),
                new CustomScoreboard(),
                new CustomTabList(),
                new ModuleList(),
                new NoAchievements(),
                new PostProcessing(),
                new TargetHUD(),
                new WaterMark(),
                //Misc
                new AntiTabComplete(),
                new Blink(),
                new ClientSpoofer(),
                new Crasher(),
                new Disabler(),
                new FakePlayer(),
                new FastPlace(),
                new HackerDetector(),
                new LightningDetector(),
                new MCF(),
                new PingSpoof(),
                //Movement
                new CorrectMovement(),
                new Eagle(),
                new FastLadder(),
                new Flight(),
                new InventoryMove(),
                new Jesus(),
                new LongJump(),
                new NoJumpDelay(),
                new NoSlowDown(),
                new NoWeb(),
                new Parkour(),
                new SafeWalk(),
                new Speed(),
                new Spider(),
                new Sprint(),
                new Step(),
                new TargetStrafe(),
                //Option
                new FontRenderer(),
                new Security(),
                new Teams(),
                new Theme(),
                //Player
                new AntiVoid(),
                new AutoArmor(),
                new AutoRespawn(),
                new AutoTool(),
                new FastBreak(),
                new InventoryManager(),
                new NoFall(),
                new NoRotate(),
                new Regen(),
                new ScaffoldWalk(),
                new Stealer(),
                new Timer(),
                //Render
                new Ambience(),
                new AntiBlind(),
                new Aspect(),
                new AttackParticles(),
                new BlockAnimations(),
                new ChinaHat(),
                new ClientCape(),
                new ESP(),
                new FullBright(),
                new HitAnimations(),
                new NameTags(),
                new ParticleMultiplier(),
                new ParticleTimer(),
                new ViewModel(),
                //Server
                new AntiBan(),
                new AutoBounty(),
                new AutoPlay(),
                new AutoReconnect(),
                new AutoHeal(),
                new AutoPig(),
                new tech.atani.client.feature.module.impl.server.sg.AntiBan()
        );
    }

    @Listen
    public void onKey(KeyInputEvent keyInputEvent) {
        this.getList()
                .stream()
                .filter(module -> module.getKey() == keyInputEvent.getKey())
                .forEach(Module::toggle);
    }

    @Override
    public <V extends Module> V getByClass(final Class<V> clazz) {
        final Module feature = this.getList().stream().filter(m -> m.getClass().equals(clazz)).findFirst().orElse(null);
        if (feature == null) return null;
        return clazz.cast(feature);
    }

    public final ArrayList<Module> getModules(Category category) {
        ArrayList<Module> modules = new ArrayList<>();
        for (Module m : this.getList()) {
            if (m.getCategory() == category)
                modules.add(m);
        }
        return modules;
    }

    public <T extends Module> T getModule(String name) {
        return (T) this.getList().stream().filter(module -> module.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public static ModuleStorage getInstance() {
        return instance;
    }

    public static void setInstance(ModuleStorage instance) {
        ModuleStorage.instance = instance;
    }
}
