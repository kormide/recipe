package ca.derekcormier.recipe.generator;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookLoader;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class Main {
  @Argument(
      index = 0,
      usage =
          "type of generation to perform; valid options: java-ingredient, java-hook, js-ingredient, js-hook, ts-ingredient, ts-hook",
      metaVar = "flavour")
  private String flavour;

  @Argument(index = 1, usage = "path to the yaml cookbook definition file", metaVar = "cookbook")
  private String cookbook;

  @Argument(index = 2, usage = "directory to output generated files", metaVar = "targetDir")
  private String targetDir;

  @Option(name = "--javaPackage", usage = "java package for generated classes", metaVar = "package")
  private String javaPackage;

  @Option(
      name = "--ingredientPostfix",
      usage = "string to append to ingredient names",
      metaVar = "postfix")
  private String ingredientPostfox;

  private CmdLineParser parser;

  public static void main(String[] args) {
    new Main().doMain(args);
  }

  public void doMain(String[] args) {
    parser = new CmdLineParser(this);

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println("could not parse arguments");
      printUsage();
      System.exit(1);
    }

    if (null == flavour || null == cookbook || null == targetDir) {
      printUsage();
      System.exit(1);
    }

    Map<String, Object> options = createOptions();

    try (FileInputStream ingredients = new FileInputStream(cookbook)) {
      Cookbook cookbook = new CookbookLoader().load(ingredients);
      Generator generator = GeneratorFactory.getGenerator(Flavour.fromAlias(flavour), cookbook);
      generator.generate(targetDir, options);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Map<String, Object> createOptions() {
    Map<String, Object> options = new HashMap<>();
    if (null != javaPackage) {
      options.put("javaPackage", javaPackage);
    }
    if (null != ingredientPostfox) {
      options.put("ingredientPostfix", ingredientPostfox);
    }
    return options;
  }

  private void printUsage() {
    System.err.println("USAGE: java -jar {jarpath} [options...] arguments...");
    parser.printUsage(System.err);
  }
}
