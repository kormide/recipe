package ca.derekcormier.recipe.generator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;

import ca.derekcormier.recipe.cookbook.Cookbook;

public class CookbookGeneratorFactoryTest {

    @Test
    public void testGetGenerator_forJavaIngredients() {
        assertTrue(GeneratorFactory.getGenerator(Flavour.JAVA_INGREDIENT, new Cookbook(new ArrayList<>(), new ArrayList<>())) instanceof JavaIngredientGenerator);
    }

    @Test
    public void testGetGenerator_forJavaHooks() {
        assertTrue(GeneratorFactory.getGenerator(Flavour.JAVA_HOOK, new Cookbook(new ArrayList<>(), new ArrayList<>())) instanceof JavaHookGenerator);
    }

    @Test
    public void testGetGenerator_forTypeScriptIngredients() {
        assertTrue(GeneratorFactory.getGenerator(Flavour.TYPESCRIPT_INGREDIENT, new Cookbook(new ArrayList<>(), new ArrayList<>())) instanceof TypeScriptIngredientGenerator);
    }

    @Test
    public void testGetGenerator_forTypeScriptHooks() {
        assertTrue(GeneratorFactory.getGenerator(Flavour.TYPESCRIPT_HOOK, new Cookbook(new ArrayList<>(), new ArrayList<>())) instanceof TypeScriptHookGenerator);
    }

    @Test
    public void testGetGenerator_forJavaScriptIngredients() {
        assertTrue(GeneratorFactory.getGenerator(Flavour.JAVASCRIPT_INGREDIENT, new Cookbook(new ArrayList<>(), new ArrayList<>())) instanceof JavaScriptIngredientGenerator);
    }

    @Test
    public void testGetGenerator_forJavaScriptHooks() {
        assertTrue(GeneratorFactory.getGenerator(Flavour.JAVASCRIPT_HOOK, new Cookbook(new ArrayList<>(), new ArrayList<>())) instanceof JavaScriptHookGenerator);
    }
}
