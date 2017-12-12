package ca.derekcormier.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Oven {
    private List<BiConsumer<String,String>> dispatchers = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SubtypeResolver subtypeResolver = new StdSubtypeResolver();

    public Oven() {
        objectMapper.setSubtypeResolver(subtypeResolver);
    }

    public void bake(Recipe recipe) {
        for (Ingredient ingredient: recipe.getIngredients()) {
            if (ingredient instanceof Recipe) {
                bake((Recipe)ingredient);
            }
            else {
                for (BiConsumer<String,String> dispatcher: dispatchers) {
                    dispatcher.accept(ingredient.getDomain(), deserializeIngredient(ingredient));
                }
            }
        }
    }

    public void addDispatcher(BiConsumer<String,String> dispatcher) {
        dispatchers.add(dispatcher);
    }

    private String deserializeIngredient(Ingredient ingredient) {
        subtypeResolver.registerSubtypes(new NamedType(ingredient.getClass(), ingredient.getType()));

        try {
            return objectMapper.writeValueAsString(ingredient);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("could not serialize recipe to json", e);
        }
    }
}
