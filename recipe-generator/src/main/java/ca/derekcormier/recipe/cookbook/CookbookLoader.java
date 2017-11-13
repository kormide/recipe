package ca.derekcormier.recipe.cookbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CookbookLoader {
    public Cookbook load(InputStream ingredients) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Cookbook cookbook = null;
        try {
            cookbook = mapper.readValue(ingredients, Cookbook.class);
        } catch (IOException e) {
            throw new RuntimeException("could not load recipe cookbook from input stream", e);
        }
        validate(cookbook);
        return cookbook;
    }

    private void validate(Cookbook cookbook) {
        validateIngredients(cookbook);
        validateEnums(cookbook);
    }

    private void validateIngredients(Cookbook cookbook) {
        validateNoDuplicateIngredientNames(cookbook);
        validateNoDuplicateFieldNames(cookbook);
        validateInitializersContainRequiredFields(cookbook);
        validateCompoundOptionalsHaveAtLeastTwoParams(cookbook);
        validateInitializerSignaturesUnique(cookbook);
    }

    private void validateEnums(Cookbook cookbook) {
        validateNoDuplicateEnumNames(cookbook);
        validateNoEmptyEnumValues(cookbook);
        validateNoDuplicateEnumValues(cookbook);
    }

    private void validateInitializersContainRequiredFields(Cookbook cookbook) {
        for (Ingredient ingredient: cookbook.getIngredients()) {
            Set<String> requiredParams = ingredient.getRequired().stream().map(r -> r.getName()).collect(Collectors.toSet());
            for (Initializer initializer: ingredient.getInitializers()) {
                for (String param: initializer.getParams()) {
                    if (!requiredParams.contains(param)) {
                        throw new RuntimeException("initializer contains undeclared param '" + param + "'");
                    }
                }
            }
        }
    }

    private void validateNoDuplicateIngredientNames(Cookbook cookbook) {
        if (cookbook.getIngredients().stream().map(Ingredient::getName).distinct().count() != cookbook.getIngredients().size()) {
            throw new RuntimeException("found two or more ingredients with the same name");
        }
    }

    private void validateNoDuplicateFieldNames(Cookbook cookbook) {
        for (Ingredient ingredient: cookbook.getIngredients()) {
            Set<String> names = new HashSet<>();
            names.addAll(ingredient.getRequired().stream().map(Required::getName).collect(Collectors.toSet()));
            names.addAll(ingredient.getOptionals().stream().map(Optional::getName).collect(Collectors.toSet()));
            names.addAll(ingredient.getCompoundOptionals().stream().map(CompoundOptional::getName).collect(Collectors.toSet()));

            if (names.size() != (ingredient.getRequired().size() + ingredient.getOptionals().size() + ingredient.getCompoundOptionals().size())) {
                throw new RuntimeException("detected duplicate field names for ingredient '" + ingredient.getName() + "'");
            }
        }
    }

    private void validateInitializerSignaturesUnique(Cookbook cookbook) {
        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Type> requiredParams = ingredient.getRequired().stream().collect(Collectors.toMap(Required::getName, Required::getType));

            List<List<Type>> signatures = new ArrayList<>();

            for (Initializer initializer: ingredient.getInitializers()) {
                List<Type> params = initializer.getParams().stream().map(requiredParams::get).collect(Collectors.toList());
                signatures.add(params);
            }

            if (signatures.stream().distinct().count() != signatures.size()) {
                throw new RuntimeException("initializer signatures for ingredient '" + ingredient.getName() + "' are ambiguous");
            }
        }
    }

    private void validateCompoundOptionalsHaveAtLeastTwoParams(Cookbook cookbook) {
        for (Ingredient ingredient: cookbook.getIngredients()) {
            for (CompoundOptional compoundOptional: ingredient.getCompoundOptionals()) {
                if (compoundOptional.getParams().size() < 2) {
                    throw new RuntimeException("compound optional '" + compoundOptional.getName() + "' has less than two params; use optional instead");
                }
            }
        }
    }

    private void validateNoDuplicateEnumNames(Cookbook cookbook) {
        Set<String> enumNames = cookbook.getEnums().stream().map(Enum::getName).collect(Collectors.toSet());
        if (enumNames.size() != cookbook.getEnums().size()) {
            throw new RuntimeException("found duplicate enum names");
        }
    }

    private void validateNoEmptyEnumValues(Cookbook cookbook) {
        if (cookbook.getEnums().stream().anyMatch(e -> e.getValues().isEmpty())) {
            throw new RuntimeException("found enum with no values");
        }
    }

    private void validateNoDuplicateEnumValues(Cookbook cookbook) {
        for (Enum e: cookbook.getEnums()) {
            if (e.getValues().stream().distinct().count() != e.getValues().size()) {
                throw new RuntimeException("enum '" + e.getName() + "' has duplicate values");
            }
        }
    }
}
