package ca.derekcormier.recipe.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Ingredient;
import liqp.filters.Filter;

public class JavaIngredientGenerator extends CookbookGenerator {
    @Override
    public void generate(Cookbook cookbook, String targetDir) {
        registerFilters(cookbook);

        String directory = createDirectories(targetDir);

        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("domain", cookbook.getDomain());
            String rendered = renderTemplate("templates/java-ingredient/ingredient.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + ".java";
            writeToFile(filepath, rendered);
        }

        for (ca.derekcormier.recipe.cookbook.Enum enumeration: cookbook.getEnums()) {
            Map<String,Object> data = new HashMap<>();
            data.put("enum", enumeration);
            String rendered = renderTemplate("templates/java-ingredient/enum.liquid", data);
            String filepath = directory + File.separator + enumeration.getName() + ".java";
            writeToFile(filepath, rendered);
        }
    }

    private void registerFilters(Cookbook cookbook) {
        Filter.registerFilter(JavaFilters.createJavaTypeFilter(cookbook));
        Filter.registerFilter(JavaFilters.createJavaValueFilter(cookbook));
    }
}
