package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Param {
  private final String name;
  private final String type;

  @JsonCreator
  public Param(
      @JsonProperty(value = "name", required = true) String name,
      @JsonProperty(value = "type", required = true) String type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }
}
