package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Ingredient;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JavaHookGenerator extends JavaGenerator {
  public JavaHookGenerator(Cookbook cookbook) {
    super(cookbook);
  }

  @Override
  public void generate(String targetDir, Map<String, Object> options) {
    if (!options.containsKey("javaPackage")) {
      options.put("javaPackage", "");
    }

    String javaPackage = (String) options.get("javaPackage");
    if (!javaPackage.isEmpty()) {
      targetDir += "/" + String.join("/", Arrays.asList(javaPackage.split("\\.")));
    }
    String directory = createDirectories(targetDir);

    Cookbook cookbook = getCookbook();
    if (!cookbook.getIngredients().isEmpty()) {
      System.out.println("Generating ingredient hooks in " + directory + "...");
    }
    for (Ingredient ingredient : cookbook.getIngredients()) {
      Map<String, Object> info = new HashMap<>();
      info.put("constantKeys", getConstantKeyValueArrays(ingredient).get(0));
      info.put("constantValues", getConstantKeyValueArrays(ingredient).get(1));

      Map<String, Object> data = new HashMap<>();
      data.put("ingredient", ingredient);
      data.put("options", options);
      data.put("info", info);
      String rendered = renderTemplate("templates/java/hook.liquid", data);
      String filepath =
          directory + File.separator + "Abstract" + ingredient.getName() + "Hook.java";
      writeToFile(filepath, rendered);
      System.out.println("  -> " + "Abstract" + ingredient.getName() + "Hook.java");
    }

    if (!cookbook.getIngredients().isEmpty()) {
      System.out.println("\nGenerating ingredient data snapshots in " + directory + "...");
    }
    for (Ingredient ingredient : cookbook.getIngredients()) {
      Map<String, Object> data = new HashMap<>();
      data.put("ingredient", ingredient);
      data.put("options", options);
      data.put("domain", cookbook.getDomain());
      String rendered = renderTemplate("templates/java/ingredient-data.liquid", data);
      String filepath = directory + File.separator + ingredient.getName() + "Data.java";
      writeToFile(filepath, rendered);
      System.out.println("  -> " + ingredient.getName() + "Data.java");
    }

    if (!cookbook.getEnums().isEmpty()) {
      System.out.println("\nGenerating hook enums in " + directory + "...");
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
