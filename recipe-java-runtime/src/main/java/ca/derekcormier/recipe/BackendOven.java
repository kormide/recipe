package ca.derekcormier.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;

import java.util.HashMap;
import java.util.Map;

public class BackendOven extends AbstractOven {
    private final Map<String,BaseIngredientHook> hooks = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SubtypeResolver subtypeResolver = new StdSubtypeResolver();

    public BackendOven() {
        objectMapper.setSubtypeResolver(subtypeResolver);
        subtypeResolver.registerSubtypes(new NamedType(Recipe.class, "Recipe"));
    }

    public String bake(String json) {
        try {
            Payload payload = objectMapper.readValue(json, Payload.class);
            Cake cake = createCake(payload.getCake());

            bakeIngredient(payload.getRecipe(), cake);

            Cake cakeToSerialize = new Cake(cake);
            return objectMapper.writeValueAsString(cakeToSerialize);
        }
        catch (Exception e) {
            throw new RuntimeException("payload serialization error", e);
        }
    }

    public void registerHook(BaseIngredientHook hook) {
        hooks.put(hook.getIngredientName(), hook);
        subtypeResolver.registerSubtypes(new NamedType(hook.getDataClass(), hook.getIngredientName()));
    }

    private void bakeIngredient(Ingredient ingredient, Cake cake) {
        if (ingredient instanceof Recipe) {
            Recipe recipe = (Recipe)ingredient;

            Runnable bakeRecipeIngredients = () -> {
                for (Ingredient i: recipe.getIngredients()) {
                    bakeIngredient(i, cake);
                }
            };

            if (recipe.getContext() != null) {
                cake.inNamespace(recipe.getContext(), bakeRecipeIngredients);
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
