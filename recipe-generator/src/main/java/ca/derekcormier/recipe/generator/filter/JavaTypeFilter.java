package ca.derekcormier.recipe.generator.filter;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.type.ArrayType;
import ca.derekcormier.recipe.cookbook.type.EnumType;
import ca.derekcormier.recipe.cookbook.type.FlagType;
import ca.derekcormier.recipe.cookbook.type.ParamType;
import ca.derekcormier.recipe.cookbook.type.PrimitiveType;
import ca.derekcormier.recipe.cookbook.type.Type;

public class JavaTypeFilter extends RecipeFilter {
    public JavaTypeFilter(Cookbook cookbook) {
        super("javatype", cookbook);
    }

    @Override
    public Object apply(Object value, Object... params) {
        ParamType type = CookbookUtils.parseType(super.asString(value), getCookbook());
        return toJavaType(type, params.length > 0 && super.asBoolean(params[0]));
    }

    private String toJavaType(ParamType type, boolean varargAsArray) {
        String javaType = _toJavaType(type.getType());
        if (type.isVararg()) {
            if (varargAsArray) {
                javaType += "[]";
            }
            else {
                javaType += "...";
            }
        }
        return javaType;
    }

    private String _toJavaType(Type type) {
        if (type instanceof PrimitiveType) {
            switch (((PrimitiveType)type).getPrimitive()) {
                case BOOLEAN:
                    return "boolean";
                case INTEGER:
                    return "int";
                case FLOAT:
                    return "float";
                case STRING:
                    return "String";
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
            return _toJavaType(((ArrayType)type).getBaseType()) + "[]";
        }
        throw new RuntimeException("unknown type");
    }
}
