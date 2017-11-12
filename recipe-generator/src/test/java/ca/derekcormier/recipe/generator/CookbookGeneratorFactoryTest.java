package ca.derekcormier.recipe.generator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CookbookGeneratorFactoryTest {

    @Test
    public void testGetGenerator_forJavaClient() {
        assertTrue(CookbookGeneratorFactory.getGenerator(Flavour.JAVA_CLIENT) instanceof JavaClientGenerator);
    }

    @Test
    public void testGetGenerator_forJavaBackend() {
        assertTrue(CookbookGeneratorFactory.getGenerator(Flavour.JAVA_BACKEND) instanceof JavaBackendGenerator);
    }
}
