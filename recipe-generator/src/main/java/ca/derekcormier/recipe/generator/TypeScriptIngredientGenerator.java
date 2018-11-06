package ca.derekcormier.recipe.generator;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.Ingredient;
import ca.derekcormier.recipe.cookbook.Optional;
import ca.derekcormier.recipe.cookbook.Param;
import ca.derekcormier.recipe.cookbook.Required;
import ca.derekcormier.recipe.cookbook.type.ArrayType;
import ca.derekcormier.recipe.cookbook.type.FlagType;
import ca.derekcormier.recipe.cookbook.type.ParamType;
import ca.derekcormier.recipe.cookbook.type.Type;

public class TypeScriptIngredientGenerator extends TypeScriptCookbookGenerator {
    public TypeScriptIngredientGenerator(Cookbook cookbook) {
        super(cookbook);
    }

    @Override
    public void generate(String domain, String targetDir, Map<String, Object> options) {
        String directory = createDirectories(targetDir);

        options.putIfAbsent("ingredientPostfix", "");

        // generate javascript ingredients
        Cookbook cookbook = getCookbook();
        if (!cookbook.getIngredients().isEmpty()) {
            System.out.println("Generating ingredients in " + directory + "...");
        }
        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> info = new HashMap<>();
            info.put("requiredTypes", getRequiredTypeMapping(ingredient));
            info.put("nonPrimitiveTypes", getNonPrimitiveTypes(ingredient, cookbook));
            info.put("constantKeys", getConstantKeyValueArrays(ingredient).get(0));
            info.put("constantValues", getConstantKeyValueArrays(ingredient).get(1));
            info.put("isVararg", getIsVarargMap(ingredient, cookbook));

            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("domain", domain);
            data.put("options", options);
            data.put("info", info);
            String rendered = renderTemplate("templates/ts/ingredient.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + options.get("ingredientPostfix") + ".js";
            writeToFile(filepath, rendered);
            System.out.println("  -> " + ingredient.getName() + options.get("ingredientPostfix") + ".js");
        }

        // generate typescript definitions for ingredients
        if (!cookbook.getIngredients().isEmpty()) {
            System.out.println("\nGenerating ingredient definitions in " + directory + "...");
        }
        for (Ingredient ingredient: cookbook.getIngredients()) {
            Map<String,Object> info = new HashMap<>();
            info.put("nonPrimitiveTypes", getNonPrimitiveTypes(ingredient, cookbook));
            info.put("constantKeys", getConstantKeyValueArrays(ingredient).get(0));
            info.put("constantValues", getConstantKeyValueArrays(ingredient).get(1));

            Map<String,Object> data = new HashMap<>();
            data.put("ingredient", ingredient);
            data.put("domain", domain);
            data.put("options", options);
            data.put("info", info);
            String rendered = renderTemplate("templates/ts/ingredient-types.liquid", data);
            String filepath = directory + File.separator + ingredient.getName() + options.get("ingredientPostfix") + ".d.ts";
            writeToFile(filepath, rendered);
            System.out.println("  -> " + ingredient.getName() + options.get("ingredientPostfix") + ".d.ts");
        }

        // generate enums
        if (!cookbook.getEnums().isEmpty()) {
            System.out.println("\nGenerating ingredient enums in " + directory + "...");
        }
        for (ca.derekcormier.recipe.cookbook.Enum enumeration: cookbook.getEnums()) {
            Map<String,Object> data = new HashMap<>();
            data.put("enum", enumeration);
            data.put("options", options);
            String rendered = renderTemplate("templates/ts/enum.liquid", data);
            String filepath = directory + File.separator + enumeration.getName() + ".js";
            writeToFile(filepath, rendered);
            System.out.println("  -> " + enumeration.getName() + ".js");
        }

        // generate enum definitions
        if (!cookbook.getEnums().isEmpty()) {
            System.out.println("\nGenerating ingredient enum definitions in " + directory + "...");
        }
        for (ca.derekcormier.recipe.cookbook.Enum enumeration: cookbook.getEnums()) {
            Map<String,Object> data = new HashMap<>();
            data.put("enum", enumeration);
            data.put("options", options);
            String rendered = renderTemplate("templates/ts/enum-types.liquid", data);
            String filepath = directory + File.separator + enumeration.getName() + ".d.ts";
            writeToFile(filepath, rendered);
            System.out.println("  -> " + enumeration.getName() + ".d.ts");
        }

        Map<String, Object> data = new HashMap<>();

        // generate index file
        System.out.println("\nGenerating index file: " + directory + File.separator + "index.js");
        data.put("ingredients", cookbook.getIngredients());
        data.put("enums", cookbook.getEnums());
        data.put("domain", domain);
        data.put("options", options);
        String rendered = renderTemplate("templates/ts/ingredient-index.liquid", data);
        String filepath = directory + File.separator + "index.js";
        writeToFile(filepath, rendered);

        // generate index definition file
        System.out.println("\nGenerating index definition file: " + directory + File.separator + "index.d.ts");
        data = new HashMap<>();
        data.put("ingredients", cookbook.getIngredients());
        data.put("enums", cookbook.getEnums());
        data.put("domain", domain);
        data.put("options", options);
        rendered = renderTemplate("templates/ts/ingredient-index-types.liquid", data);
        filepath = directory + File.separator + "index.d.ts";
        writeToFile(filepath, rendered);
    }

    private Map<String,String> getRequiredTypeMapping(Ingredient ingredient) {
        Map<String,String> requiredTypes = new HashMap<>();

        for (Required required: ingredient.getRequired()) {
            requiredTypes.put(required.getName(), required.getType());
        }

        return requiredTypes;
    }

    private Map<String,Boolean> getIsVarargMap(Ingredient ingredient, Cookbook cookbook) {
        Map<String,Boolean> isVararg = new HashMap<>();

        for (Required required: ingredient.getRequired()) {
            isVararg.put(required.getName(), CookbookUtils.parseType(required.getType(), cookbook).isVararg());
        }

        return isVararg;
    }

    private Set<String> getNonPrimitiveTypes(Ingredient ingredient, Cookbook cookbook) {
        Set<String> types = new HashSet<>();
        for (Required required: ingredient.getRequired()) {
            if (isNonPrimitive(required.getType(), cookbook)) {
                types.add(getBaseType(required.getType(), cookbook));
            }
        }

        for (Optional optional: ingredient.getOptionals()) {
            if (!optional.isCompound() && isNonPrimitive(optional.getType(), cookbook)) {
                types.add(getBaseType(optional.getType(), cookbook));
            }
            else if (optional.isCompound()) {
                for (Param param: optional.getParams()) {
                    if (isNonPrimitive(param.getType(), cookbook)) {
                        types.add(getBaseType(param.getType(), cookbook));
                    }
                }
            }
        }

        return types;
    }

    private boolean isNonPrimitive(String type, Cookbook cookbook) {
        Type t = CookbookUtils.parseType(type, cookbook).getType();
        return !CookbookUtils.isPrimitiveType(t) && !(t instanceof FlagType);
    }

    private String getBaseType(String type, Cookbook cookbook) {
        ParamType paramType = CookbookUtils.parseType(type, cookbook);

        if (paramType.getType() instanceof ArrayType) {
            return getBaseType(((ArrayType)paramType.getType()).getBaseType().name(), cookbook);
        }
        else {
            return paramType.getType().name();
        }
    }
}
