package ca.derekcormier.recipe.generator;

import java.io.FileInputStream;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookLoader;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            printUsage();
            return;
        }
        try (FileInputStream ingredients = new FileInputStream(args[1])) {
            Cookbook domain = new CookbookLoader().load(ingredients);
            CookbookGenerator generator = CookbookGeneratorFactory.getGenerator(Flavour.fromAlias(args[0]));
            generator.generate(domain, args[2]);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void printUsage() {
        System.err.println(String.join("\n",
            "USAGE: java -jar {jarpath} flavour cookbook target_dir",
            "  flavour     - type of generation to perform; valid options: java-ingredient, java-hook",
            "  cookbook    - path to the yaml cookbook definition file",
            "  target_dir  - directory to output generated files"
        ));
    }
}
