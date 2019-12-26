package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Cookbook {
  private final List<Ingredient> ingredients;
  private final List<Enum> enums;

  @JsonCreator
  public Cookbook(
      @JsonProperty(value = "ingredients") List<Ingredient> ingredients,
      @JsonProperty(value = "enums") List<Enum> enums) {
    this.ingredients = ingredients == null ? new ArrayList<>() : ingredients;
    this.enums = enums == null ? new ArrayList<>() : enums;
  }

  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public List<Enum> getEnums() {
    return enums;
  }
}
