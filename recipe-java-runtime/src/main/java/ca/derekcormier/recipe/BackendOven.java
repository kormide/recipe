package ca.derekcormier.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BackendOven {
    private final Map<String,BaseIngredientHook> hooks = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SubtypeResolver subtypeResolver = new StdSubtypeResolver();

    public BackendOven() {
        objectMapper.setSubtypeResolver(subtypeResolver);
        subtypeResolver.registerSubtypes(new NamedType(RecipeData.class, "Recipe"));
    }

    public void bake(String json) {
        try {
            IngredientSnapshot ingredient = objectMapper.readValue(json, IngredientSnapshot.class);
            bakeIngredient(ingredient);
        } catch (IOException e) {
            throw new RuntimeException("could not deserialize json ingredient", e);
        }
    }

    public void registerHook(BaseIngredientHook hook) {
        hooks.put(hook.getIngredientName(), hook);
        subtypeResolver.registerSubtypes(new NamedType(hook.getDataClass(), hook.getIngredientName()));
    }

    private void bakeIngredient(IngredientSnapshot ingredient) {
        if (ingredient instanceof RecipeData) {
            ((RecipeData)ingredient).getIngredients().forEach(this::bakeIngredient);
        }
        else {
            if (hooks.containsKey(ingredient.getType())) {
                hooks.get(ingredient.getType()).bake(ingredient);
            }
            else {
                throw new RuntimeException("could not find hook for ingredient " + ingredient.getType());
            }
        }

    }
}
