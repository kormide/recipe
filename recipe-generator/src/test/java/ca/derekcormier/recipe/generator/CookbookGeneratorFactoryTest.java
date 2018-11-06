package ca.derekcormier.recipe.generator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;

import ca.derekcormier.recipe.cookbook.Cookbook;

public class CookbookGeneratorFactoryTest {

    @Test
    public void testGetGenerator_forJavaIngredients() {
        assertTrue(CookbookGeneratorFactory.getGenerator(Flavour.JAVA_INGREDIENT, new Cookbook(new ArrayList<>(), new ArrayList<>())) instanceof JavaIngredientGenerator);
    }

    @Test
    public void testGetGenerator_forJavaHooks() {
        assertTrue(CookbookGeneratorFactory.getGenerator(Flavour.JAVA_HOOK, new Cookbook(new ArrayList<>(), new ArrayList<>())) instanceof JavaHookGenerator);
    }

    @Test
    public void testGetGenerator_forTypescriptIngredients() {
        assertTrue(CookbookGeneratorFactory.getGenerator(Flavour.TYPESCRIPT_INGREDIENT, new Cookbook(new ArrayList<>(), new ArrayList<>())) instanceof TypeScriptIngredientGenerator);
    }
}
