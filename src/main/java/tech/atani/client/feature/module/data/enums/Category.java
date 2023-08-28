package tech.atani.client.feature.module.data.enums;

public enum Category {
    COMBAT("Combat"), MOVEMENT("Movement"), PLAYER("Player"), MISCELLANEOUS("Others"), SERVER("Server"), RENDER("Render"), HUD("Design"), OPTIONS("Options");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
