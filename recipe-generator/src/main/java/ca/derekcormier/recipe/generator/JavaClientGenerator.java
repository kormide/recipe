package ca.derekcormier.recipe.generator;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.Ingredient;
import ca.derekcormier.recipe.cookbook.Type;
import liqp.Template;
import liqp.filters.Filter;

public class JavaClientGenerator extends CookbookGenerator {
    @Override
    public void generate(Cookbook cookbook) {
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

        Template template = loadTemplate("templates/java-client/ingredient.liquid");

        for (Ingredient ingredient: cookbook.getIngredients()) {
            String rendered = null;
            try {
                Map<String,Object> data = new HashMap<>();
                data.put("ingredient", ingredient);
                rendered = template.render(new ObjectMapper().writeValueAsString(data));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            System.out.println(rendered);
        }
    }
}
