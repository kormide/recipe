package ca.derekcormier.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BackendOven {
    private final Map<String,AbstractIngredientHook> hooks = new HashMap<>();
    private final Map<String, Class<? extends IngredientData>> ingredientRegister = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BackendOven() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(IngredientData.class, new IngredientDeserializer(ingredientRegister));
        objectMapper.registerModule(module);
        ingredientRegister.put("Recipe", RecipeData.class);
    }

    public void bake(String json) {
        try {
            IngredientData ingredient = objectMapper.readValue(json, IngredientData.class);
            bakeIngredient(ingredient);
        } catch (IOException e) {
            throw new RuntimeException("could not deserialize json ingredient", e);
        }
    }

    public void registerHook(AbstractIngredientHook hook) {
        hooks.put(hook.getIngredientName(), hook);
        ingredientRegister.put(hook.getIngredientName(), hook.getDataClass());
    }

    private void bakeIngredient(IngredientData ingredient) {
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
