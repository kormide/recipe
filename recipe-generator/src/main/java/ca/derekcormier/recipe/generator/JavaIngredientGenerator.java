package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Ingredient;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JavaIngredientGenerator extends JavaGenerator {
  public JavaIngredientGenerator(Cookbook cookbook) {
    super(cookbook);
  }

  @Override
  public void generate(String domain, String targetDir, Map<String, Object> options) {
    if (!options.containsKey("javaPackage")) {
      options.put("javaPackage", "");
    }

    options.putIfAbsent("ingredientPostfix", "");

    String javaPackage = (String) options.get("javaPackage");
    if (!javaPackage.isEmpty()) {
      targetDir += "/" + String.join("/", Arrays.asList(javaPackage.split("\\.")));
    }
    String directory = createDirectories(targetDir);

    Cookbook cookbook = getCookbook();
    if (!cookbook.getIngredients().isEmpty()) {
      System.out.println("Generating ingredients in " + directory + "...");
    }
    for (Ingredient ingredient : cookbook.getIngredients()) {
      Map<String, Object> info = new HashMap<>();
      info.put("constantKeys", getConstantKeyValueArrays(ingredient).get(0));
      info.put("constantValues", getConstantKeyValueArrays(ingredient).get(1));

      Map<String, Object> data = new HashMap<>();
      data.put("ingredient", ingredient);
      data.put("domain", domain);
      data.put("options", options);
      data.put("info", info);

      String rendered = renderTemplate("templates/java/ingredient.liquid", data);
      String filepath =
          directory
              + File.separator
              + ingredient.getName()
              + options.get("ingredientPostfix")
              + ".java";
      writeToFile(filepath, rendered);
      System.out.println(
          "  -> " + ingredient.getName() + options.get("ingredientPostfix") + ".java");
    }

    if (!cookbook.getEnums().isEmpty()) {
      System.out.println("\nGenerating ingredient enums in " + directory + "...");
    }
    for (ca.derekcormier.recipe.cookbook.Enum enumeration : cookbook.getEnums()) {
      Map<String, Object> data = new HashMap<>();
      data.put("enum", enumeration);
      data.put("options", options);
      String rendered = renderTemplate("templates/java/enum.liquid", data);
      String filepath = directory + File.separator + enumeration.getName() + ".java";
      writeToFile(filepath, rendered);
      System.out.println("  -> " + enumeration.getName() + ".java");
    }
  }
}
