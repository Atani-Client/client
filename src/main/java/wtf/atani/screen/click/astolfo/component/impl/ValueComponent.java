package wtf.atani.screen.click.astolfo.component.impl;

import wtf.atani.screen.click.astolfo.component.Component;
import wtf.atani.value.Value;

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
