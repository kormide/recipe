package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class Optional {
  private final String name;
  private final String type;
  private final List<Param> params;
  private final boolean repeatable;
  private final boolean compound;

  @JsonCreator
  public Optional(
      @JsonProperty(value = "name", required = true) String name,
      @JsonProperty(value = "type") String type,
      @JsonProperty(value = "params") List<Param> params,
      @JsonProperty(value = "repeatable") boolean repeatable) {
    this.name = name;
    this.type = type;
    this.compound = params != null;
    this.params = this.compound ? params : new ArrayList<>();
    this.repeatable = repeatable;

    if (!(type == null ^ params == null)) {
      throw new RuntimeException("optional cannot have both type and params");
    }

    if (this.compound && params.isEmpty()) {
      throw new RuntimeException("compound optional params cannot be empty");
    }

    if (this.compound && params.stream().anyMatch(p -> CookbookUtils.isFlagType(p.getType()))) {
      throw new RuntimeException("compound optionals cannot have a flag parameter");
    }
  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public List<Param> getParams() {
    return params;
  }

  public boolean isRepeatable() {
    return repeatable;
  }

  public boolean isCompound() {
    return compound;
  }
}
