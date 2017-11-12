package ca.derekcormier.recipe;

import java.io.FileInputStream;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookLoader;
import ca.derekcormier.recipe.generator.CookbookGenerator;
import ca.derekcormier.recipe.generator.CookbookGeneratorFactory;
import ca.derekcormier.recipe.generator.Flavour;

public class Main {
    public static void main(String[] args) {
        try(FileInputStream ingredients = new FileInputStream(args[0])) {
            Cookbook domain = new CookbookLoader().load(ingredients);
            CookbookGenerator generator = CookbookGeneratorFactory.getGenerator(Flavour.JAVA_CLIENT);
            generator.generate(domain);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
