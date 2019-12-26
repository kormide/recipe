package ca.derekcormier.recipe.cookbook.type;

import ca.derekcormier.recipe.cookbook.Primitive;

public class PrimitiveType extends Type {
  private final Primitive primitive;

  public PrimitiveType(Primitive primitive) {
    this.primitive = primitive;
  }

  public Primitive getPrimitive() {
    return primitive;
  }

  @Override
  public String name() {
    return primitive.getAlias();
  }
}
