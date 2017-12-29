package ca.derekcormier.recipe.cookbook.type;

public class ArrayType extends Type {
    private final Type baseType;

    public ArrayType(Type baseType) {
        this.baseType = baseType;
    }

    public Type getBaseType() {
        return baseType;
    }

    @Override
    public String name() {
        return baseType.name() + "[]";
    }
}
