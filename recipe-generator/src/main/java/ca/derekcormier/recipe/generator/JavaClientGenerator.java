package ca.derekcormier.recipe.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Ingredient;
import ca.derekcormier.recipe.cookbook.Type;
import liqp.filters.Filter;

public class JavaClientGenerator extends CookbookGenerator {
    @Override
    public void generate(Cookbook cookbook, String targetDir) {
        Filter.registerFilter(new Filter("javatype") {
            @Override
            public Object apply(Object value, Object... params) {
                Type type = Type.fromAlias(super.asString(value));
                switch(type) {
                    case STRING:
                        return "String";
                    case BOOLEAN:
                        return "boolean";
                }
                throw new RuntimeException("unknown data type '" + super.asString(value) + "'");
            }
        });

        String directory = createDirectories(targetDir);

        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            String rendered = rendered = renderTemplate("templates/java-client/ingredient.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + ".java";
            writeToFile(filepath, rendered);
        }
    }
}
