package ca.derekcormier.recipe.generator;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Ingredient;
import ca.derekcormier.recipe.generator.filter.JavaParamFilter;
import ca.derekcormier.recipe.generator.filter.JavaTypeFilter;
import ca.derekcormier.recipe.generator.filter.JavaValueFilter;
import liqp.filters.Filter;

public class JavaIngredientGenerator extends CookbookGenerator {
    @Override
    public void generate(String domain, Cookbook cookbook, String targetDir, Map<String,Object> options) {
        registerFilters(cookbook);

        if (!options.containsKey("javaPackage")) {
            options.put("javaPackage", "");
        }

        options.putIfAbsent("ingredientPostfix", "");

        String javaPackage = (String)options.get("javaPackage");
        if (!javaPackage.isEmpty()) {
            targetDir += "/" + String.join("/", Arrays.asList(javaPackage.split("\\.")));
        }
        String directory = createDirectories(targetDir);

        if (!cookbook.getIngredients().isEmpty()) {
            System.out.println("Generating ingredients in " + directory + "...");
        }
        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> info = new HashMap<>();
            info.put("constantKeys", getConstantKeyValueArrays(ingredient).get(0));
            info.put("constantValues", getConstantKeyValueArrays(ingredient).get(1));

            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("domain", domain);
            data.put("options", options);
            data.put("info", info);

            String rendered = renderTemplate("templates/java/ingredient.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + options.get("ingredientPostfix") + ".java";
            writeToFile(filepath, rendered);
            System.out.println("  -> " + ingredient.getName() + options.get("ingredientPostfix") + ".java");
        }

        if (!cookbook.getEnums().isEmpty()) {
            System.out.println("\nGenerating ingredient enums in " + directory + "...");
        }
        for (ca.derekcormier.recipe.cookbook.Enum enumeration: cookbook.getEnums()) {
            Map<String,Object> data = new HashMap<>();
            data.put("enum", enumeration);
            data.put("options", options);
            String rendered = renderTemplate("templates/java/enum.liquid", data);
            String filepath = directory + File.separator + enumeration.getName() + ".java";
            writeToFile(filepath, rendered);
            System.out.println("  -> " + enumeration.getName() + ".java");
        }
    }

    private void registerFilters(Cookbook cookbook) {
        Filter.registerFilter(new JavaParamFilter(cookbook, new JavaTypeFilter(cookbook)));
        Filter.registerFilter(new JavaTypeFilter(cookbook));
        Filter.registerFilter(new JavaValueFilter(cookbook));
    }
}
