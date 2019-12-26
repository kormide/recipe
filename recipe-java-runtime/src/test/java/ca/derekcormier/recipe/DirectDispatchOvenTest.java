package ca.derekcormier.recipe;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Test;

public class DirectDispatchOvenTest {
  @Test
  public void testBake_dispatchesToBackendOven() {
    BackendOven backendOven = spy(BackendOven.class);
    backendOven.registerHook(new FooIngredientHook());
    Oven oven = new DirectDispatchOven(backendOven);

    oven.bake(Recipe.prepare(new Ingredient("FooIngredient", "FooDomain") {}));

    verify(backendOven).bake(anyString());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testAddDispatcher_throws() {
    Oven oven = new DirectDispatchOven(new BackendOven());

    oven.addDispatcher("FooDomain", payload -> "{}");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testSetDefaultDispatcher_throws() {
    Oven oven = new DirectDispatchOven(new BackendOven());

    oven.setDefaultDispatcher(payload -> "{}");
  }
}

class FooIngredientData extends Ingredient {
  public FooIngredientData() {
    super("FooIngredient", "FooDomain");
  }
}

class FooIngredientHook extends BaseIngredientHook<FooIngredientData> {
  public FooIngredientHook() {
    super("FooIngredient", FooIngredientData.class);
  }

  @Override
  public void bake(FooIngredientData ingredient, Cake cake) {}
}
