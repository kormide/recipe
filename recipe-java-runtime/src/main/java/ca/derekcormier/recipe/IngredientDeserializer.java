package ca.derekcormier.recipe;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Map;

public class IngredientDeserializer extends StdDeserializer<IngredientData> {
    private final Map<String,Class<? extends IngredientData>> ingredientDataClasses;

    public IngredientDeserializer(Map<String,Class<? extends IngredientData>> ingredientDataClasses) {
        super((Class<?>)null);
        this.ingredientDataClasses = ingredientDataClasses;
    }

    @Override
    public IngredientData deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String type = node.fieldNames().next();
        String ingredientJson = node.get(type).toString();
        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();

        if (type.equals("Recipe")) {
            return mapper.readValue(ingredientJson, RecipeData.class);
        }
        return new ObjectMapper().readValue(ingredientJson, ingredientDataClasses.get(type));
    }
}
