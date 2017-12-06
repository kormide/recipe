package ca.derekcormier.recipe;

import java.io.FileInputStream;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookLoader;
import ca.derekcormier.recipe.generator.CookbookGenerator;
import ca.derekcormier.recipe.generator.CookbookGeneratorFactory;
import ca.derekcormier.recipe.generator.Flavour;

public class Main {
    public static void main(String[] args) {
        try(FileInputStream ingredients = new FileInputStream(args[1])) {
            Cookbook domain = new CookbookLoader().load(ingredients);
            CookbookGenerator generator = CookbookGeneratorFactory.getGenerator(Flavour.fromAlias(args[0]));
            generator.generate(domain, args[2]);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
