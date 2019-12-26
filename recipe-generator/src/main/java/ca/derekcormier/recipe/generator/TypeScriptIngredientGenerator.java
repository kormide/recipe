package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.Ingredient;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TypeScriptIngredientGenerator extends TypeScriptGenerator {
  public TypeScriptIngredientGenerator(Cookbook cookbook) {
    super(cookbook);
  }

  @Override
  public void generate(String targetDir, Map<String, Object> options) {
    String directory = createDirectories(targetDir);

    options.putIfAbsent("ingredientPostfix", "");
    options.putIfAbsent("runtimeLibrary", "recipe-ts-runtime");

    // generate all of the javascript stuff
    JavaScriptIngredientGenerator jsGenerator = new JavaScriptIngredientGenerator(getCookbook());
    jsGenerator.generate(targetDir, options);

    Cookbook cookbook = getCookbook();

    // generate typescript definitions for ingredients
    if (!cookbook.getIngredients().isEmpty()) {
      System.out.println("\nGenerating ingredient definitions in " + directory + "...");
    }
    for (Ingredient ingredient : cookbook.getIngredients()) {
      Map<String, Object> info = new HashMap<>();
      info.put("nonPrimitiveTypes", CookbookUtils.getNonPrimitiveTypes(ingredient, cookbook));
      info.put("constantKeys", getConstantKeyValueArrays(ingredient).get(0));
      info.put("constantValues", getConstantKeyValueArrays(ingredient).get(1));

      Map<String, Object> data = new HashMap<>();
      data.put("ingredient", ingredient);
      data.put("domain", cookbook.getDomain());
      data.put("options", options);
      data.put("info", info);
      String rendered = renderTemplate("templates/ts/ingredient-types.liquid", data);
      String filepath =
          directory
              + File.separator
              + ingredient.getName()
              + options.get("ingredientPostfix")
              + ".d.ts";
      writeToFile(filepath, rendered);
      System.out.println(
          "  -> " + ingredient.getName() + options.get("ingredientPostfix") + ".d.ts");
    }

    // generate enum definitions
    if (!cookbook.getEnums().isEmpty()) {
      System.out.println("\nGenerating ingredient enum definitions in " + directory + "...");
    }
    for (ca.derekcormier.recipe.cookbook.Enum enumeration : cookbook.getEnums()) {
      Map<String, Object> data = new HashMap<>();
      data.put("enum", enumeration);
      data.put("options", options);
      String rendered = renderTemplate("templates/ts/enum-types.liquid", data);
      String filepath = directory + File.separator + enumeration.getName() + ".d.ts";
      writeToFile(filepath, rendered);
      System.out.println("  -> " + enumeration.getName() + ".d.ts");
    }

    // generate index definition file
    Map<String, Object> data = new HashMap<>();
    System.out.println(
        "\nGenerating index definition file: " + directory + File.separator + "index.d.ts");
    data.put("ingredients", cookbook.getIngredients());
    data.put("enums", cookbook.getEnums());
    data.put("domain", cookbook.getDomain());
    data.put("options", options);
    String rendered = renderTemplate("templates/ts/ingredient-index-types.liquid", data);
    String filepath = directory + File.separator + "index.d.ts";
    writeToFile(filepath, rendered);
  }
}
