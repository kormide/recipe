package ca.derekcormier.recipe.generator.filter;

import ca.derekcormier.recipe.cookbook.Cookbook;
import liqp.filters.Filter;

public class RecipeFilter extends Filter {
  private final Cookbook cookbook;

  public RecipeFilter(String name, Cookbook cookbook) {
    super(name);
    this.cookbook = cookbook;
  }

  protected Cookbook getCookbook() {
    return cookbook;
  }
}
