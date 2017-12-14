package ca.derekcormier.recipe;

@FunctionalInterface
public interface Dispatcher {
    String dispatch(String domain, String payload);
}
