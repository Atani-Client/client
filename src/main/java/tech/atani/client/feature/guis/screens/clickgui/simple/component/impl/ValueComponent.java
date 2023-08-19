package tech.atani.client.feature.guis.screens.clickgui.simple.component.impl;

import tech.atani.client.feature.guis.screens.clickgui.simple.component.Component;
import tech.atani.client.feature.module.value.Value;

public abstract class ValueComponent extends Component {

    protected final Value value;

    public ValueComponent(Value value, float posX, float posY, float baseWidth, float baseHeight) {
        super(posX, posY, baseWidth, baseHeight);
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
