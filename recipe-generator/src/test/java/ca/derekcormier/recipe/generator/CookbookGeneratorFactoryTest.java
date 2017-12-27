package ca.derekcormier.recipe.generator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CookbookGeneratorFactoryTest {

    @Test
    public void testGetGenerator_forJavaIngredients() {
        assertTrue(CookbookGeneratorFactory.getGenerator(Flavour.JAVA_INGREDIENT) instanceof JavaIngredientGenerator);
    }

    @Test
    public void testGetGenerator_forJavaHooks() {
        assertTrue(CookbookGeneratorFactory.getGenerator(Flavour.JAVA_HOOK) instanceof JavaHookGenerator);
    }

    @Test
    public void testGetGenerator_forTypescriptIngredients() {
        assertTrue(CookbookGeneratorFactory.getGenerator(Flavour.TYPESCRIPT_INGREDIENT) instanceof TypescriptIngredientGenerator);
    }
}
