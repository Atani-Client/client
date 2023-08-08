package wtf.atani.module.data.enums;

public enum Category {
    COMBAT("Combat"), MOVEMENT("Movement"), PLAYER("Player"),MISCELLANEOUS("Miscellaneous"), RENDER("Render"), HUD("HUD and GUIs");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
