package ca.derekcormier.recipe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Oven {
    private List<Dispatcher> dispatchers = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SubtypeResolver subtypeResolver = new StdSubtypeResolver();

    public Oven() {
        objectMapper.setSubtypeResolver(subtypeResolver);
    }

    public Cake bake(Recipe recipe) {
        registerSubtypes(recipe);
        Cake cake = _bake(recipe, new Cake());
        return cake;
    }

    private Cake _bake(Recipe recipe, Cake cake) {
        try {
            List<Recipe.Segment> segments = recipe.segment();
            for (Recipe.Segment segment: segments) {
                String payload = serializePayload(segment.recipe, cake);
                for (Dispatcher dispatcher: dispatchers) {
                    String jsonCake = dispatcher.dispatch(segment.domain, payload);
                    cake = deserializeCake(jsonCake);
                }
            }
            return cake;
        }
        catch (Exception e) {
            throw new RuntimeException("could not bake cake", e);
        }
    }

    public void addDispatcher(Dispatcher dispatcher) {
        dispatchers.add(dispatcher);
    }

    private String serializePayload(Recipe recipe, Cake cake) throws JsonProcessingException {
        Payload payload = new Payload(recipe, cake);
        return objectMapper.writeValueAsString(payload);
    }

    private Cake deserializeCake(String json) throws IOException {
        return objectMapper.readValue(json, Cake.class);
    }

    private void registerSubtypes(Recipe recipe) {
        if (recipe.getContextIngredient() != null) {
            subtypeResolver.registerSubtypes(new NamedType(recipe.getContextIngredient().getClass(), recipe.getContextIngredient().getType()));
        }
        for (Ingredient ingredient: recipe.getIngredients()) {
            if (ingredient instanceof Recipe) {
                registerSubtypes((Recipe)ingredient);
            }
            else {
                subtypeResolver.registerSubtypes(new NamedType(ingredient.getClass(), ingredient.getType()));
            }
        }
    }
}
