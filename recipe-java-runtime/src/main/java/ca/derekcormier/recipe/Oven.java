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
        Cake cake = new Cake();
        registerSubtypes(recipe);
        _bake(recipe, cake);
        return cake;
    }

    private Cake _bake(Recipe recipe, Cake cake) {
        for (Ingredient ingredient: recipe.getIngredients()) {
            if (ingredient instanceof Recipe) {
                cake = _bake((Recipe)ingredient, cake);
            }
            else {
                String payload = serializePayload(ingredient, cake);
                for (Dispatcher dispatcher: dispatchers) {
                    String jsonCake = dispatcher.dispatch(ingredient.getDomain(), payload);
                    cake = deserializeCake(jsonCake);
                }
            }
        }
        return cake;
    }

    public void addDispatcher(Dispatcher dispatcher) {
        dispatchers.add(dispatcher);
    }

    private String serializePayload(Ingredient ingredient, Cake cake) {
        try {
            Payload payload = new Payload(ingredient, cake);
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("could not serialize recipe to json", e);
        }
    }

    private Cake deserializeCake(String json) {
        try {
            return objectMapper.readValue(json, Cake.class);
        } catch (IOException e) {
            throw new RuntimeException("could not deserialize cake");
        }
    }

    private void registerSubtypes(Recipe recipe) {
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
