package ca.derekcormier.recipe;

public abstract class AbstractOven {
    protected Cake createCake() {
        return new Cake();
    }

    protected Cake createCake(Cake other) {
        return new Cake(other);
    }
}
