package ca.derekcormier.recipe.cookbook.type;

public class EnumType extends Type {
    private final String name;

    public EnumType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
