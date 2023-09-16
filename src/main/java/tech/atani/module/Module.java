package tech.atani.module;

import tech.atani.Client;
import tech.atani.utils.interfaces.IMethods;
import tech.atani.value.Value;
import net.minecraft.util.Formatting;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class Module implements IMethods {
    public Info info = this.getClass().getAnnotation(Info.class);

    public String name = info.name();

    public Category category = info.category();

    public int keyBind = info.keyBind();

    public String suffix = "";

    public boolean autoEnabled = info.autoEnabled(), toggled;

    private final List<Value> values = new ArrayList<>();

    public void setToggled(final boolean toggled) {
        this.toggled = toggled;

        if (this.toggled) {
            if (mc.player != null) onEnable();
            Client.INSTANCE.getEventBus().register(this);
        } else {
            Client.INSTANCE.getEventBus().unregister(this);
            if (mc.player != null) onDisable();
        }
    }

    public <T extends Value> T getValueByName(String name) {
        return (T) values.stream().filter(value -> value.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public String getDisplayName() {
        String name = getName();

        if (!suffix.isEmpty() || !suffix.equals(""))
            name += " " + Formatting.WHITE + suffix;

        return name;
    }

    protected void onEnable() { }
    protected void onDisable() { }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setKeyBind(int keyBind) {
        this.keyBind = keyBind;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public Info getInfo() {
        return info;
    }

    public int getKeyBind() {
        return keyBind;
    }

    public void toggle() {
        this.setToggled(!toggled);
    }

    public List<Value> getValues() {
        return values;
    }

    public enum Category {
        COMBAT("Combat"),
        MOVEMENT("Movement"),
        PLAYER("Player"),
        EXPLOIT("Exploit"),
        VISUAL("Visual"),
        SCRIPTS("Scripts");

        private String name;

        public String getName() {
            return name;
        }

        Category(String name) {
            this.name = name;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Info {
        String name();

        Category category();

        int keyBind() default 0;

        boolean autoEnabled() default false;
    }
}
