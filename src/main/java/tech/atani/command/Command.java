package tech.atani.command;

import tech.atani.utils.interfaces.IMethods;

public abstract class Command implements IMethods {
    private final String[] expressions;

    public Command(final String... expressions) {
        this.expressions = expressions;
    }

    public abstract void execute(String[] args);

    public String[] getExpressions() {
        return expressions;
    }
}
