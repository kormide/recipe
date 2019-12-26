package ca.derekcormier.recipe;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class KeyedIngredient extends Ingredient {
  private String key;

  public KeyedIngredient(String name, String domain) {
    super(name, domain);
  }

  protected KeyedIngredient(KeyedIngredient other) {
    super(other);
    this.key = other.key;
  }

  public KeyedIngredient keyed(String key) {
    this.key = key;
    return this;
  }

  public String getKey() {
    return key;
  }

  protected void setKey(String key) {
    this.key = key;
  }

  public boolean hasKey() {
    return null != key;
  }
}
