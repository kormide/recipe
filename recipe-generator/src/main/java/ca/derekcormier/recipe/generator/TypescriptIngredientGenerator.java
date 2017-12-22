package ca.derekcormier.recipe.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Ingredient;

public class TypescriptIngredientGenerator extends CookbookGenerator {
    @Override
    public void generate(Cookbook cookbook, String targetDir, Map<String, Object> options) {
        String directory = createDirectories(targetDir);

        // generate javascript ingredients
        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("domain", cookbook.getDomain());
            data.put("options", options);
            String rendered = renderTemplate("templates/ts/ingredient.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + ".js";
            writeToFile(filepath, rendered);
        }

        // generate typescript definitions for ingredients
        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("domain", cookbook.getDomain());
            data.put("options", options);
            String rendered = renderTemplate("templates/ts/ingredient-types.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + ".d.ts";
            writeToFile(filepath, rendered);
        }
    }
}
