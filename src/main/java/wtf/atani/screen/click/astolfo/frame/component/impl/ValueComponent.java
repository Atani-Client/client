package wtf.atani.screen.click.astolfo.frame.component.impl;

import wtf.atani.screen.click.astolfo.frame.component.Component;
import wtf.atani.value.Value;

public abstract class ValueComponent extends Component {

    private final Value value;

    public ValueComponent(Value value, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.value = value;
    }

    public Value getValue() {
        return value;
    }
}
