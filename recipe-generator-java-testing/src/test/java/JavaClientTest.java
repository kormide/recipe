import org.junit.Test;

public class JavaClientTest {
    @Test
    public void testGeneration_emptyIngredient() {
        new EmptyIngredient();
    }

    @Test
    public void testGeneration_ingredientWithRequiredProperty() {
        new IngredientWithRequiredProperty("foo");
    }
}
