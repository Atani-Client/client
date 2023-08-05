package wtf.atani.screen.click.icarus.window.component.impl;

import wtf.atani.screen.click.icarus.window.component.Component;
import wtf.atani.value.Value;

public abstract class ValueComponent extends Component {

    private final Value value;

    public ValueComponent(Value value, float posX, float posY, float height) {
        super(posX, posY, height);
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
