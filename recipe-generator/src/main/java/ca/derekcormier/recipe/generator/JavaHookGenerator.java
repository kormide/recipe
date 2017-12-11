package ca.derekcormier.recipe.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Ingredient;
import liqp.filters.Filter;

public class JavaHookGenerator extends CookbookGenerator {
    @Override
    public void generate(Cookbook cookbook, String targetDir) {
        registerFilters(cookbook);
        String directory = createDirectories(targetDir);

        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            String rendered = renderTemplate("templates/java-hook/hook.liquid", data);
            String filepath = directory + File.separator + "Abstract" + ingredient.getName() + "Hook.java";
            writeToFile(filepath, rendered);
        }

        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            String rendered = renderTemplate("templates/java-hook/ingredient-data.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + "Data.java";
            writeToFile(filepath, rendered);
        }
    }

    private void registerFilters(Cookbook cookbook) {
        Filter.registerFilter(JavaFilters.createJavaTypeFilter(cookbook));
    }
}
