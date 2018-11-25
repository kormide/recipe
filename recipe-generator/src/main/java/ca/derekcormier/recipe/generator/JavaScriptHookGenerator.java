package ca.derekcormier.recipe.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.Ingredient;

public class JavaScriptHookGenerator extends JavaScriptGenerator {
    public JavaScriptHookGenerator(Cookbook cookbook) {
        super(cookbook);
    }

    @Override
    public void generate(String domain, String targetDir, Map<String, Object> options) {
        options.putIfAbsent("ingredientPostfix", "");
        options.putIfAbsent("runtimeLibrary", "recipe-js-runtime");

        String directory = createDirectories(targetDir);

        generateAbstractHooks(directory, options);
        generateIngredientDataSnapshots(directory, options, domain);
        generateEnums(directory, options);
        generateIndex(directory);
    }

    private void generateAbstractHooks(String directory, Map<String, Object> options) {
        if (!getCookbook().getIngredients().isEmpty()) {
            System.out.println("Generating ingredient hooks in " + directory + "...");
        }
        for (Ingredient ingredient: getCookbook().getIngredients()) {
            Map<String,Object> info = new HashMap<>();
            info.put("constantKeys", getConstantKeyValueArrays(ingredient).get(0));
            info.put("constantValues", getConstantKeyValueArrays(ingredient).get(1));

            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("options", options);
            data.put("info", info);
            String rendered = renderTemplate("templates/js/hook.liquid", data);
            String filepath = directory + File.separator + "Abstract" + ingredient.getName() + "Hook.js";
            writeToFile(filepath, rendered);
            System.out.println("  -> " + "Abstract" + ingredient.getName() + "Hook.js");
        }
    }

    private void generateIngredientDataSnapshots(String directory, Map<String, Object> options, String domain) {
        if (!getCookbook().getIngredients().isEmpty()) {
            System.out.println("\nGenerating ingredient data snapshots in " + directory + "...");
        }
        for (Ingredient ingredient: getCookbook().getIngredients()) {
            Map<String,Object> info = new HashMap<>();
            info.put("nonPrimitiveTypes", CookbookUtils.getNonPrimitiveTypes(ingredient, getCookbook()));

            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("options", options);
            data.put("domain", domain);
            data.put("info", info);
            String rendered = renderTemplate("templates/js/ingredient-data.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + "Data.js";
            writeToFile(filepath, rendered);
            System.out.println("  -> " + ingredient.getName() + "Data.js");
        }
    }

    private void generateEnums(String directory, Map<String, Object> options) {
        Cookbook cookbook = getCookbook();
        if (!cookbook.getEnums().isEmpty()) {
            System.out.println("\nGenerating hook enums in " + directory + "...");
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
    }

    private void generateIndex(String directory) {
        Map<String, Object> data = new HashMap<>();
        System.out.println("\nGenerating index file: " + directory + File.separator + "index.js");
        data.put("ingredients", getCookbook().getIngredients());
        data.put("enums", getCookbook().getEnums());
        String rendered = renderTemplate("templates/js/hook-index.liquid", data);
        String filepath = directory + File.separator + "index.js";
        writeToFile(filepath, rendered);
    }
}
