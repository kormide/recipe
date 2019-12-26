package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.Ingredient;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TypeScriptHookGenerator extends TypeScriptGenerator {
  public TypeScriptHookGenerator(Cookbook cookbook) {
    super(cookbook);
  }

  @Override
  public void generate(String targetDir, Map<String, Object> options) {
    options.put("runtimeLibrary", "recipe-ts-runtime");
    String directory = createDirectories(targetDir);

    new JavaScriptHookGenerator(getCookbook()).generate(targetDir, options);

    generateAbstractHookTypes(directory, options);
    generateIngredientDataSnapshotTypes(directory, options, getCookbook().getDomain());
    generateEnumTypes(directory, options);
    generateIndexTypes(directory);
  }

  private void generateAbstractHookTypes(String directory, Map<String, Object> options) {
    if (!getCookbook().getIngredients().isEmpty()) {
      System.out.println("Generating ingredient hook types in " + directory + "...");
    }
    for (Ingredient ingredient : getCookbook().getIngredients()) {
      Map<String, Object> info = new HashMap<>();
      info.put("constantKeys", getConstantKeyValueArrays(ingredient).get(0));
      info.put("constantValues", getConstantKeyValueArrays(ingredient).get(1));

      Map<String, Object> data = new HashMap<>();
      data.put("ingredient", ingredient);
      data.put("options", options);
      data.put("info", info);
      String rendered = renderTemplate("templates/ts/hook-types.liquid", data);
      String filepath =
          directory + File.separator + "Abstract" + ingredient.getName() + "Hook.d.ts";
      writeToFile(filepath, rendered);
      System.out.println("  -> " + "Abstract" + ingredient.getName() + "Hook.d.ts");
    }
  }

  private void generateIngredientDataSnapshotTypes(
      String directory, Map<String, Object> options, String domain) {
    if (!getCookbook().getIngredients().isEmpty()) {
      System.out.println("\nGenerating ingredient data snapshot types in " + directory + "...");
    }
    for (Ingredient ingredient : getCookbook().getIngredients()) {
      Map<String, Object> info = new HashMap<>();
      info.put("nonPrimitiveTypes", CookbookUtils.getNonPrimitiveTypes(ingredient, getCookbook()));

      Map<String, Object> data = new HashMap<>();
      data.put("ingredient", ingredient);
      data.put("options", options);
      data.put("info", info);

      String rendered = renderTemplate("templates/ts/ingredient-data-types.liquid", data);
      String filepath = directory + File.separator + ingredient.getName() + "Data.d.ts";
      writeToFile(filepath, rendered);
      System.out.println("  -> " + ingredient.getName() + "Data.d.ts");
    }
  }

  private void generateEnumTypes(String directory, Map<String, Object> options) {
    if (!getCookbook().getEnums().isEmpty()) {
      System.out.println("\nGenerating hook enum definitions in " + directory + "...");
    }
    for (ca.derekcormier.recipe.cookbook.Enum enumeration : getCookbook().getEnums()) {
      Map<String, Object> data = new HashMap<>();
      data.put("enum", enumeration);
      data.put("options", options);
      String rendered = renderTemplate("templates/ts/enum-types.liquid", data);
      String filepath = directory + File.separator + enumeration.getName() + ".d.ts";
      writeToFile(filepath, rendered);
      System.out.println("  -> " + enumeration.getName() + ".d.ts");
    }
  }

  private void generateIndexTypes(String directory) {
    Map<String, Object> data = new HashMap<>();
    System.out.println("\nGenerating index file: " + directory + File.separator + "index.d.ts");
    data.put("ingredients", getCookbook().getIngredients());
    data.put("enums", getCookbook().getEnums());
    String rendered = renderTemplate("templates/ts/hook-index-types.liquid", data);
    String filepath = directory + File.separator + "index.d.ts";
    writeToFile(filepath, rendered);
  }
}
