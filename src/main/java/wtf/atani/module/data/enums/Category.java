package wtf.atani.module.data.enums;

public enum Category {
    COMBAT("Combat"), PLAYER("Player"), MOVEMENT("Movement"), MISCELLANEOUS("Miscellaneous"), RENDER("Render"), HUD("HUD");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
