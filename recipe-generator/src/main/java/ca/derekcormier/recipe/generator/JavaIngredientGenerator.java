package ca.derekcormier.recipe.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Ingredient;
import liqp.filters.Filter;

public class JavaIngredientGenerator extends CookbookGenerator {
    @Override
    public void generate(Cookbook cookbook, String targetDir, Map<String,Object> options) {
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

        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> info = new HashMap<>();
            info.put("constantKeys", getConstantKeyValueArrays(ingredient).get(0));
            info.put("constantValues", getConstantKeyValueArrays(ingredient).get(1));

            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("domain", cookbook.getDomain());
            data.put("options", options);
            data.put("info", info);
            try {
                System.out.println(new ObjectMapper().writeValueAsString(data));
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            String rendered = renderTemplate("templates/java/ingredient.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + options.get("ingredientPostfix") + ".java";
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
        Filter.registerFilter(JavaFilters.createJavaValueFilter(cookbook));
    }
}
