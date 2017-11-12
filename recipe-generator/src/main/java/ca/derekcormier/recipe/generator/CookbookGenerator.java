package ca.derekcormier.recipe.generator;

import java.io.InputStream;
import java.util.Scanner;

import ca.derekcormier.recipe.cookbook.Cookbook;
import liqp.RenderSettings;
import liqp.Template;

public abstract class CookbookGenerator {
    public abstract void generate(Cookbook cookbook);

    protected Template loadTemplate(String path) {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        String template = scanner.hasNext() ? scanner.next() : "";
        return Template.parse(template).withRenderSettings(new RenderSettings.Builder().withStrictVariables(true).build());
    }
}
