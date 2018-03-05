package ca.derekcormier.recipe;

public abstract class AbstractOven {
    protected Cake createCake() {
        return new Cake();
    }
}
