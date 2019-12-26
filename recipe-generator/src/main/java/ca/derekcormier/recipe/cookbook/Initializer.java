package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Initializer {
  private final List<String> params;

  @JsonCreator
  public Initializer(@JsonProperty(value = "params") List<String> params) {
    this.params = params == null ? new ArrayList<>() : params;
  }

  public List<String> getParams() {
    return params;
  }
}
