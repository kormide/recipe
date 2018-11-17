package ca.derekcormier.recipe.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.Ingredient;
import ca.derekcormier.recipe.cookbook.Required;

public class JavaScriptIngredientGenerator extends JavaScriptGenerator {
    public JavaScriptIngredientGenerator(Cookbook cookbook) {
        super(cookbook);
    }

    @Override
    public void generate(String domain, String targetDir, Map<String, Object> options) {
        String directory = createDirectories(targetDir);

        options.putIfAbsent("ingredientPostfix", "");
        options.putIfAbsent("runtimeLibrary", "recipe-js-runtime");

        // generate javascript ingredients
        Cookbook cookbook = getCookbook();
        if (!cookbook.getIngredients().isEmpty()) {
            System.out.println("Generating ingredients in " + directory + "...");
        }
        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> info = new HashMap<>();
            info.put("requiredTypes", getRequiredTypeMapping(ingredient));
            info.put("nonPrimitiveTypes", CookbookUtils.getNonPrimitiveTypes(ingredient, cookbook));
            info.put("constantKeys", getConstantKeyValueArrays(ingredient).get(0));
            info.put("constantValues", getConstantKeyValueArrays(ingredient).get(1));
            info.put("isVararg", getIsVarargMap(ingredient, cookbook));

            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("domain", domain);
            data.put("options", options);
            data.put("info", info);
            String rendered = renderTemplate("templates/js/ingredient.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + options.get("ingredientPostfix") + ".js";
            writeToFile(filepath, rendered);
            System.out.println("  -> " + ingredient.getName() + options.get("ingredientPostfix") + ".js");
        }

        // generate enums
        if (!cookbook.getEnums().isEmpty()) {
            System.out.println("\nGenerating ingredient enums in " + directory + "...");
        }
        for (ca.derekcormier.recipe.cookbook.Enum enumeration: cookbook.getEnums()) {
            Map<String,Object> data = new HashMap<>();
            data.put("enum", enumeration);
            data.put("options", options);
            String rendered = renderTemplate("templates/js/enum.liquid", data);
            String filepath = directory + File.separator + enumeration.getName() + ".js";
            writeToFile(filepath, rendered);
            System.out.println("  -> " + enumeration.getName() + ".js");
        }

        // generate index file
        Map<String, Object> data = new HashMap<>();
        System.out.println("\nGenerating index file: " + directory + File.separator + "index.js");
        data.put("ingredients", cookbook.getIngredients());
        data.put("enums", cookbook.getEnums());
        data.put("domain", domain);
        data.put("options", options);
        String rendered = renderTemplate("templates/js/ingredient-index.liquid", data);
        String filepath = directory + File.separator + "index.js";
        writeToFile(filepath, rendered);
    }

    private Map<String,String> getRequiredTypeMapping(Ingredient ingredient) {
        Map<String,String> requiredTypes = new HashMap<>();

        for (Required required: ingredient.getRequired()) {
            requiredTypes.put(required.getName(), required.getType());
        }

        return requiredTypes;
    }

    private Map<String,Boolean> getIsVarargMap(Ingredient ingredient, Cookbook cookbook) {
        Map<String, Boolean> isVararg = new HashMap<>();

        for (Required required : ingredient.getRequired()) {
            isVararg.put(required.getName(), CookbookUtils.parseType(required.getType(), cookbook).isVararg());
        }

        return isVararg;
    }
}
