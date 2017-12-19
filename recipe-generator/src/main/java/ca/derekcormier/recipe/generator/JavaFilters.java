package ca.derekcormier.recipe.generator;

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

public class JavaFilters {
    public static Filter createJavaTypeFilter(Cookbook cookbook) {
        return new Filter("javatype") {
            @Override
            public Object apply(Object value, Object... params) {
                ParamType type = CookbookUtils.parseType(super.asString(value), cookbook);
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
        };
    }

    public static Filter createJavaValueFilter(Cookbook cookbook) {
        return new Filter("javavalue") {
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
                            case STRING:
                                return "\"" + super.asString(value) + "\"";
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
}
