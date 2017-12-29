package ca.derekcormier.recipe.generator;

import java.util.Map;

import ca.derekcormier.recipe.cookbook.Cookbook;
import ca.derekcormier.recipe.cookbook.CookbookUtils;
import ca.derekcormier.recipe.cookbook.Primitive;
import ca.derekcormier.recipe.cookbook.type.ArrayType;
import ca.derekcormier.recipe.cookbook.type.EnumType;
import ca.derekcormier.recipe.cookbook.type.FlagType;
import ca.derekcormier.recipe.cookbook.type.ParamType;
import ca.derekcormier.recipe.cookbook.type.PrimitiveType;
import ca.derekcormier.recipe.cookbook.type.Type;
import liqp.filters.Filter;

public class TypescriptFilters {
    public static Filter createTsTypeFilter(Cookbook cookbook) {
        return new Filter("tstype") {
            @Override
            public Object apply(Object value, Object... params) {
                ParamType type = CookbookUtils.parseType(super.asString(value), cookbook);
                return TypescriptFilters.toTsType(type);
            }
        };
    }

    public static Filter createTsParamFilter(Cookbook cookbook) {
        return new Filter("tsparam") {
            @Override
            public Object apply(Object value, Object... params) {
                Map<String,String> param = (Map<String,String>)value;
                String name = param.get("name");
                ParamType type = CookbookUtils.parseType(param.get("type"), cookbook);

                return (type.isVararg() ? "..." : "") + name + ": " + TypescriptFilters.toTsType(type);
            }
        };
    }

    public static Filter createJsParamFilter(Cookbook cookbook) {
        return new Filter("jsparam") {
            @Override
            public Object apply(Object value, Object... params) {
                Map<String,String> param = (Map<String,String>)value;
                String name = param.get("name");
                ParamType type = CookbookUtils.parseType(param.get("type"), cookbook);

                return (type.isVararg() ? "..." : "") + name;
            }
        };
    }

    public static Filter createTsValueFilter(Cookbook cookbook) {
        return new Filter("tsvalue") {
            @Override
            public Object apply(Object value, Object... params) {
                String strType = super.asString(params[0]);

                if (CookbookUtils.isKnownType(cookbook, strType)) {
                    if (CookbookUtils.isPrimitiveType(strType)) {
                        Primitive type = Primitive.fromAlias(strType);
                        switch (type) {
                            case BOOLEAN:
                                return super.asString(value);
                            case INTEGER:
                                return super.asString(value);
                            case FLOAT:
                                return super.asString(value);
                            case STRING:
                                return "\"" + super.asString(value).replace("\"", "\\\"") + "\"";
                            default:
                                throw new RuntimeException("unknown data type '" + strType + "'");
                        }
                    }
                    else if (CookbookUtils.isEnumType(cookbook, strType)) {
                        if (CookbookUtils.enumHasValue(cookbook, strType, super.asString(value))) {
                            return strType + "." + super.asString(value);
                        }

                        throw new RuntimeException("value '" + super.asString(value) + "' is not a member of enum '" + strType + "'");
                    }
                }

                throw new RuntimeException("unknown data type '" + super.asString(value) + "'");
            }
        };
    }

    private static String toTsType(ParamType type) {
        return _toTsType(type.getType()) + (type.isVararg() ? "[]" : "");
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
                    return "string";
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
            return _toTsType(((ArrayType)type).getBaseType()) + "[]";
        }
        throw new RuntimeException("unknown type");
    }
}

