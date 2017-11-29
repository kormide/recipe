package ca.derekcormier.recipe.generator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.Ingredient;
import ca.derekcormier.recipe.cookbook.PrimitiveType;
import liqp.filters.Filter;

public class JavaClientGenerator extends CookbookGenerator {
    @Override
    public void generate(Cookbook cookbook, String targetDir) {
        Filter.registerFilter(new Filter("javatype") {
            @Override
            public Object apply(Object value, Object... params) {
                if (CookbookUtils.isPrimitiveType(super.asString(value))) {
                    PrimitiveType type = PrimitiveType.fromAlias(super.asString(value));
                    switch(type) {
                        case BOOLEAN:
                            return "boolean";
                        case INTEGER:
                            return "int";
                        case STRING:
                            return "String";

                        default:
                            throw new RuntimeException("unknown data type '" + super.asString(value) + "'");
                    }
                }
                else if (CookbookUtils.isKnownType(cookbook,super.asString(value))) {
                    return super.asString(value);
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

        for (ca.derekcormier.recipe.cookbook.Enum enumeration: cookbook.getEnums()) {
            Map<String,Object> data = new HashMap<>();
            data.put("enum", enumeration);
            String rendered = rendered = renderTemplate("templates/java-client/enum.liquid", data);
            String filepath = directory + File.separator + enumeration.getName() + ".java";
            writeToFile(filepath, rendered);
        }
    }
}
