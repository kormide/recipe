package ca.derekcormier.recipe.generator.filter;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.Primitive;
import ca.derekcormier.recipe.cookbook.type.ArrayType;
import ca.derekcormier.recipe.cookbook.type.EnumType;
import ca.derekcormier.recipe.cookbook.type.FlagType;
import ca.derekcormier.recipe.cookbook.type.ParamType;
import ca.derekcormier.recipe.cookbook.type.PrimitiveType;
import ca.derekcormier.recipe.cookbook.type.Type;

public class TsTypeFilter extends RecipeFilter {
    public TsTypeFilter(Cookbook cookbook) {
        super("tstype", cookbook);
    }

    public static String toTsType(ParamType type) {
        boolean isWrapped = type.isVararg() && type.getType() instanceof PrimitiveType && ((PrimitiveType)type.getType()).getPrimitive() == Primitive.STRING;
        return (isWrapped ? "(" : "") + _toTsType(type.getType()) + (isWrapped ? ")" : "") + (type.isVararg() ? "[]" : "");
    }

    private static String _toTsType(Type type) {
        if (type instanceof PrimitiveType) {
            switch (((PrimitiveType)type).getPrimitive()) {
                case BOOLEAN:
                    return "boolean";
                case INTEGER:
                    return "number";
                case FLOAT:
                    return "number";
                case STRING:
                    return "string | null";
                default:
                    throw new RuntimeException("unknown type");
            }
        }
        else if (type instanceof FlagType) {
            return "boolean";
        }
        else if (type instanceof EnumType) {
            return ((EnumType)type).getName();
        }
        else if (type instanceof ArrayType) {
            boolean isWrapped = ((ArrayType)type).getBaseType() instanceof PrimitiveType && ((PrimitiveType)((ArrayType)type).getBaseType()).getPrimitive() == Primitive.STRING;
            return (isWrapped ? "(" : "") + _toTsType(((ArrayType)type).getBaseType()) + (isWrapped ? ")" : "") + "[]";
        }
        throw new RuntimeException("unknown type");
    }


    @Override
    public Object apply(Object value, Object... params) {
        ParamType type = CookbookUtils.parseType(super.asString(value), getCookbook());
        return toTsType(type);
    }
}
