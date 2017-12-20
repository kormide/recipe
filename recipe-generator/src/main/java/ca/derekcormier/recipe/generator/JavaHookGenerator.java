package ca.derekcormier.recipe.generator;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Ingredient;
import liqp.filters.Filter;

public class JavaHookGenerator extends CookbookGenerator {
    @Override
    public void generate(Cookbook cookbook, String targetDir, Map<String,Object> options) {
        registerFilters(cookbook);

        if (!options.containsKey("javaPackage")) {
            options.put("javaPackage", "");
        }

        String javaPackage = (String)options.get("javaPackage");
        if (!javaPackage.isEmpty()) {
            targetDir += "/" + String.join("/", Arrays.asList(javaPackage.split("\\.")));
        }
        String directory = createDirectories(targetDir);

        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("options", options);
            String rendered = renderTemplate("templates/java/hook.liquid", data);
            String filepath = directory + File.separator + "Abstract" + ingredient.getName() + "Hook.java";
            writeToFile(filepath, rendered);
        }

        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("options", options);
            String rendered = renderTemplate("templates/java/ingredient-data.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + "Data.java";
            writeToFile(filepath, rendered);
        }

        for (ca.derekcormier.recipe.cookbook.Enum enumeration: cookbook.getEnums()) {
            Map<String,Object> data = new HashMap<>();
            data.put("enum", enumeration);
            data.put("options", options);
            String rendered = renderTemplate("templates/java/enum.liquid", data);
            String filepath = directory + File.separator + enumeration.getName() + ".java";
            writeToFile(filepath, rendered);
        }
    }

    private void registerFilters(Cookbook cookbook) {
        Filter.registerFilter(JavaFilters.createJavaTypeFilter(cookbook));
    }
}
