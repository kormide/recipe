package ca.derekcormier.recipe.cookbook.type;

public class ParamType {
  private final Type type;
  private final boolean vararg;

  public ParamType(Type type, boolean vararg) {
    this.type = type;
    this.vararg = vararg;
  }

  public Type getType() {
    return type;
  }

  public boolean isVararg() {
    return vararg;
  }
}
