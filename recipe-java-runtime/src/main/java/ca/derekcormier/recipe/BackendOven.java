package ca.derekcormier.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;

import java.util.HashMap;
import java.util.Map;

public class BackendOven {
    private final Map<String,BaseIngredientHook> hooks = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SubtypeResolver subtypeResolver = new StdSubtypeResolver();

    public BackendOven() {
        objectMapper.setSubtypeResolver(subtypeResolver);
        subtypeResolver.registerSubtypes(new NamedType(RecipeSnapshot.class, "Recipe"));
    }

    public String bake(String json) {
        try {
            BackendPayload payload = objectMapper.readValue(json, BackendPayload.class);
            Cake cake = payload.getCake();
            bakeIngredient(payload.getRecipe(), cake);
            return objectMapper.writeValueAsString(cake);
        }
        catch (Exception e) {
            throw new RuntimeException("payload serialization error", e);
        }
    }

    public void registerHook(BaseIngredientHook hook) {
        hooks.put(hook.getIngredientName(), hook);
        subtypeResolver.registerSubtypes(new NamedType(hook.getDataClass(), hook.getIngredientName()));
    }

    private void bakeIngredient(IngredientSnapshot ingredient, Cake cake) {
        if (ingredient instanceof RecipeSnapshot) {
            RecipeSnapshot recipe = (RecipeSnapshot)ingredient;

            Runnable bakeRecipeIngredients = () -> {
                for (IngredientSnapshot i: recipe.getIngredients()) {
                    bakeIngredient(i, cake);
                }
            };

            if (recipe.getContext() != null) {
                cake.inNamespace(recipe.getContext(), bakeRecipeIngredients);
            }
            else if (recipe.getContextIngredient() != null) {
                bakeIngredient(recipe.getContextIngredient(), cake);
                if (recipe.getContextIngredient().getKey() != null) {
                    cake.inNamespace(recipe.getContextIngredient().getKey(), bakeRecipeIngredients);
                }
                else {
                    bakeRecipeIngredients.run();
                }
            }
            else {
                bakeRecipeIngredients.run();
            }
        }
        else {
            hooks.get(ingredient.getIngredientType()).bake(ingredient, cake);
        }
    }
}
